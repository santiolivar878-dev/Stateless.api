let player;
let deviceId;
let lastSection = 'section-home';
let progressTicker = null;
let lastKnownState = null;
let lastStateTimestamp = 0;
let currentTrackId = null;

const statusEl = document.getElementById('status');
const playPauseBtn = document.getElementById('play-pause');

async function fetchToken() {
    try {
        const res = await fetch('/api/token');

        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(
                `HTTP ${res.status}: ${errorText || 'No se pudo obtener el token'}`
            );
        }

        const data = await res.json();

        return data.token;

    } catch (error) {
        console.error('Error al obtener token:', error);

        statusEl.textContent =
            'Error: No se pudo obtener el token. ' + error.message;

        throw error;
    }
}


/* =========================================================
   SPOTIFY PLAYER
   ========================================================= */

window.onSpotifyWebPlaybackSDKReady = () => {

    player = new Spotify.Player({
        name: 'STATELESS Player',

        getOAuthToken: cb => {
            fetchToken()
                .then(token => cb(token))
                .catch(() => {
                    statusEl.textContent =
                        'Error: Fallo en autenticación';
                });
        },

        volume: 0.5
    });


    player.addListener('ready', async ({ device_id }) => {

        deviceId = device_id;

        statusEl.textContent =
            'Transfiriendo reproduccion...';

        try {

            const response = await fetch(
                `/api/player/transfer?deviceId=${device_id}`,
                { method: 'PUT' }
            );

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

        } catch (error) {

            console.warn(
                'Advertencia al transferir:',
                error
            );

        } finally {

            statusEl.textContent = '';
            document.getElementById('sidebar-status').textContent = 'spotify connected';
        }
    });


    player.addListener(
        'not_ready',
        () => statusEl.textContent =
            'Dispositivo desconectado.'
    );


    player.addListener(
        'initialization_error',
        ({ message }) =>
            statusEl.textContent =
                'Error: ' + message
    );


    player.addListener(
        'authentication_error',
        ({ message }) =>
            statusEl.textContent =
                'Error de autenticacion: ' + message
    );


    player.addListener(
        'account_error',
        () =>
            statusEl.textContent =
                'Se requiere cuenta Spotify Premium.'
    );


    player.addListener(
        'player_state_changed',
        state => {

            if (!state) return;

            lastKnownState = state;
            lastStateTimestamp = Date.now();

            updateNowPlayingUI(state);

            startProgressTicker();
        }
    );


    player.connect();
};


window.addEventListener('load', () => {

    setTimeout(() => {

        if (!window.Spotify) {

            statusEl.textContent =
                'Error: No se pudo cargar el SDK de Spotify.';

        }

    }, 5000);
});


/* =========================================================
   CONTROLES
   ========================================================= */

playPauseBtn.addEventListener(
    'click',
    () => player?.togglePlay()
);


document.getElementById('next').addEventListener(
    'click',
    () => player?.nextTrack()
);


document.getElementById('prev').addEventListener(
    'click',
    () => player?.previousTrack()
);

let repeatMode = 'off';

document.getElementById('shuffle').addEventListener('click', async event => {
    const enabled = event.currentTarget.dataset.enabled !== 'true';
    await player?.toggleShuffle({ state: enabled });
    event.currentTarget.dataset.enabled = String(enabled);
    event.currentTarget.classList.toggle('is-active', enabled);
});

document.getElementById('repeat').addEventListener('click', async event => {
    repeatMode = repeatMode === 'off' ? 'context' : repeatMode === 'context' ? 'track' : 'off';
    try {
        const response = await fetch(`/api/player/repeat?state=${repeatMode}&deviceId=${encodeURIComponent(deviceId ?? '')}`, { method: 'PUT' });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
    } catch (error) {
        console.error('Error al cambiar repetición:', error);
        statusEl.textContent = 'No se pudo cambiar el modo de repetición.';
        return;
    }
    event.currentTarget.dataset.mode = repeatMode;
    event.currentTarget.classList.toggle('is-active', repeatMode !== 'off');
    event.currentTarget.title = repeatMode === 'track' ? 'Repetir canción' : repeatMode === 'context' ? 'Repetir playlist' : 'Repetir apagado';
    document.getElementById('expanded-repeat').classList.toggle('is-active', repeatMode !== 'off');
});

document.getElementById('queue').addEventListener('click', () => {
    statusEl.textContent = 'La cola de Spotify se controla desde tu cuenta.';
});

document.getElementById('device').addEventListener('click', () => {
    statusEl.textContent = deviceId ? 'Reproduciendo en STATELESS Player.' : 'Dispositivo aún no conectado.';
});

function openExpandedPlayer() {
    const image = document.getElementById('mini-cover');
    if (!image.src) return;
    document.getElementById('expanded-cover').src = image.src;
    document.getElementById('expanded-track-name').textContent = document.getElementById('mini-track-name').textContent;
    document.getElementById('expanded-artist-name').textContent = document.getElementById('mini-artist-name').textContent;
    document.getElementById('expanded-album-name').textContent = document.getElementById('home-album-name').textContent;
    document.getElementById('expanded-player').hidden = false;
    document.body.classList.add('expanded-player-open');
}

document.getElementById('mini-cover-button').addEventListener('click', openExpandedPlayer);
document.getElementById('mini-expand').addEventListener('click', openExpandedPlayer);
document.getElementById('expanded-player-close').addEventListener('click', () => {
    document.getElementById('expanded-player').hidden = true;
    document.body.classList.remove('expanded-player-open');
});

document.getElementById('expanded-play').addEventListener('click', () => player?.togglePlay());
document.getElementById('expanded-prev').addEventListener('click', () => player?.previousTrack());
document.getElementById('expanded-next').addEventListener('click', () => player?.nextTrack());
document.getElementById('expanded-shuffle').addEventListener('click', () => document.getElementById('shuffle').click());
document.getElementById('expanded-repeat').addEventListener('click', () => document.getElementById('repeat').click());

document.getElementById('account-button').addEventListener('click', async () => {
    const panel = document.getElementById('account-panel');
    panel.hidden = false;
    try {
        const response = await fetch('/api/player/profile');
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const profile = await response.json();
        document.getElementById('account-display-name').textContent = profile.display_name || profile.id || 'Cuenta Spotify';
        document.getElementById('account-email').textContent = profile.email || `${profile.product || 'Spotify'} · ${profile.country || ''}`;
        const image = profile.images?.[0]?.url;
        if (image) {
            document.getElementById('account-image').src = image;
            document.getElementById('account-avatar').src = image;
            document.getElementById('account-avatar').hidden = false;
            document.getElementById('account-avatar-fallback').hidden = true;
        }
    } catch (error) {
        console.error('Error al cargar la cuenta:', error);
        document.getElementById('account-email').textContent = 'No se pudo cargar el perfil de Spotify.';
    }
});

document.getElementById('account-close').addEventListener('click', () => {
    document.getElementById('account-panel').hidden = true;
});
document.getElementById('account-profile').addEventListener('click', () => {
    document.getElementById('account-email').scrollIntoView({ behavior: 'smooth' });
});
document.getElementById('account-recent').addEventListener('click', () => {
    document.getElementById('account-panel').hidden = true;
    document.getElementById('nav-home').click();
    document.getElementById('recent-see-all').click();
});
document.getElementById('account-help').addEventListener('click', () => {
    statusEl.textContent = 'Reproduce música, busca en Spotify y guarda tus canciones desde LIFE.';
});
document.getElementById('account-private').addEventListener('click', event => {
    event.currentTarget.classList.toggle('is-active');
    statusEl.textContent = event.currentTarget.classList.contains('is-active') ? 'Sesión privada activada.' : 'Sesión privada desactivada.';
});
document.getElementById('account-preferences').addEventListener('click', () => {
    statusEl.textContent = 'Preferencias disponibles desde Spotify.';
});


document.getElementById('mini-volume').addEventListener(
    'input',
    e => {
        player?.setVolume(
            Number(e.target.value) / 100
        );
    }
);

document.getElementById('home-progress-bar').addEventListener(
    'click',
    event => {
        if (!lastKnownState?.duration) return;

        const progressBar = event.currentTarget;
        const position = (event.offsetX / progressBar.clientWidth) * lastKnownState.duration;

        player?.seek(Math.max(0, Math.min(position, lastKnownState.duration)));
    }
);

document.getElementById('expanded-progress-bar').addEventListener('click', event => {
    if (!lastKnownState?.duration) return;
    const progressBar = event.currentTarget;
    const bounds = progressBar.getBoundingClientRect();
    const position = ((event.clientX - bounds.left) / bounds.width) * lastKnownState.duration;
    player?.seek(Math.max(0, Math.min(position, lastKnownState.duration)));
});


window.addEventListener(
    'beforeunload',
    () => player?.disconnect()
);


/* =========================================================
   ACTUALIZAR NOW PLAYING
   ========================================================= */

function formatTime(ms) {

    const totalSec =
        Math.floor(ms / 1000);

    const min =
        Math.floor(totalSec / 60);

    const sec =
        totalSec % 60;

    return `${min}:${sec
        .toString()
        .padStart(2, '0')}`;
}


function updateNowPlayingUI(state) {

    const track =
        state.track_window.current_track;

    const coverUrl =
        track.album.images?.[0]?.url ?? '';

    const trackName =
        track.name;

    const artistName =
        track.artists
            .map(a => a.name)
            .join(', ');


    document.getElementById(
        'mini-cover'
    ).src = coverUrl;


    document.getElementById(
        'mini-track-name'
    ).textContent = trackName;


    document.getElementById(
        'mini-artist-name'
    ).textContent = artistName;

    document.getElementById('sidebar-cover').src = coverUrl;
    document.getElementById('sidebar-track-name').textContent = trackName;
    document.getElementById('sidebar-artist-name').textContent = artistName;


    playPauseBtn.src = state.paused

        ? '/player/skins/imagenes/life/boton_play.png'

        : '/player/skins/imagenes/life/boton_pausa.png';


    document.getElementById(
        'home-cover'
    ).src = coverUrl;


    document.getElementById(
        'home-track-name'
    ).textContent = trackName;


    document.getElementById(
        'home-artist-name'
    ).textContent = artistName;


    document.getElementById(
        'home-album-name'
    ).textContent =
        track.album.name;

    document.getElementById('expanded-cover').src = coverUrl;
    document.getElementById('expanded-track-name').textContent = trackName;
    document.getElementById('expanded-artist-name').textContent = artistName;
    document.getElementById('expanded-album-name').textContent = track.album.name;
    document.getElementById('expanded-play').textContent = state.paused ? '▶' : 'Ⅱ';


    document.getElementById(
        'home-time-total'
    ).textContent =
        formatTime(state.duration);


    checkLikedStatus(track.id);
}

async function loadCurrentTrack() {
    try {
        const response = await fetch('/api/player/current');

        if (response.status === 204) return;

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const responseText = await response.text();
        if (!responseText.trim()) return;

        const current = JSON.parse(responseText);
        const track = current.item;

        if (!track?.album || !track.artists?.length) return;

        const state = {
            track_window: { current_track: track },
            paused: current.is_playing === false,
            position: current.progress_ms ?? 0,
            duration: track.duration_ms ?? 0
        };

        lastKnownState = state;
        lastStateTimestamp = Date.now();
        updateNowPlayingUI(state);
        startProgressTicker();
    } catch (error) {
        console.error('Error al cargar la canción actual:', error);
    }
}

async function loadAccountProfile() {
    try {
        const response = await fetch('/api/player/profile');
        if (!response.ok) return;
        const profile = await response.json();
        const displayName = profile.display_name || profile.id || 'Cuenta Spotify';
        document.getElementById('account-display-name').textContent = displayName;
        document.getElementById('account-email').textContent = profile.email || `${profile.product || 'Spotify'} · ${profile.country || ''}`;
        const image = profile.images?.[0]?.url;
        if (image) {
            document.getElementById('account-image').src = image;
            document.getElementById('account-avatar').src = image;
            document.getElementById('account-avatar').hidden = false;
            document.getElementById('account-avatar-fallback').hidden = true;
        }
    } catch (error) {
        console.error('Error al cargar perfil:', error);
    }
}


/* =========================================================
   PROGRESO
   ========================================================= */

function startProgressTicker() {

    if (progressTicker)
        clearInterval(progressTicker);


    progressTicker = setInterval(() => {

        if (!lastKnownState)
            return;


        const elapsed =
            lastKnownState.paused
                ? 0
                : (Date.now() - lastStateTimestamp);


        const position =
            Math.min(
                lastKnownState.position + elapsed,
                lastKnownState.duration
            );


        const pct =
            lastKnownState.duration
                ? (position / lastKnownState.duration) * 100
                : 0;


        document.getElementById(
            'home-progress-fill'
        ).style.width =
            pct + '%';


        document.getElementById(
            'home-time-current'
        ).textContent =
            formatTime(position);

        document.getElementById('expanded-time-current').textContent = formatTime(position);
        document.getElementById('expanded-time-total').textContent = formatTime(lastKnownState.duration);
        document.getElementById('expanded-progress-fill').style.width = pct + '%';

    }, 1000);
}


/* =========================================================
   ME GUSTA
   ========================================================= */

async function checkLikedStatus(trackId) {

    currentTrackId = trackId;

    try {

        const res =
            await fetch(
                `/api/player/is-liked?trackId=${trackId}`
            );

        if (!res.ok)
            return;


        const liked =
            await res.json();


        setLikeIcon(liked);

    } catch (error) {

        console.error(
            'Error al chequear like:',
            error
        );
    }
}


function setLikeIcon(liked) {

    ['like-btn', 'mini-like-btn'].forEach(id => {
        const icon = document.getElementById(id);
        if (!icon) return;
        icon.src = liked
            ? '/player/skins/imagenes/life/Me_Gusta_relleno.png'
            : '/player/skins/imagenes/life/Me_Gusta_previo.png';
        icon.dataset.liked = liked ? 'true' : 'false';
    });
}


document.getElementById(
    'like-btn'
).addEventListener(
    'click',
    async () => {

        if (!currentTrackId)
            return;


        const isLiked =
            document.getElementById(
                'like-btn'
            ).dataset.liked === 'true';


        try {

            if (isLiked) {

                await fetch(
                    `/api/player/unlike?trackId=${currentTrackId}`,
                    { method: 'POST' }
                );

                setLikeIcon(false);

            } else {

                await fetch(
                    `/api/player/like?trackId=${currentTrackId}`,
                    { method: 'PUT' }
                );

                setLikeIcon(true);
            }

        } catch (error) {

            console.error(
                'Error al alternar like:',
                error
            );
        }
    }
);

document.getElementById('mini-like-btn').addEventListener('click', () => {
    document.getElementById('like-btn').click();
});


/* =========================================================
   FECHA
   ========================================================= */

function renderHomeDate() {

    const now = new Date();


    const dateStr =
        now.toLocaleDateString(
            'en-US',
            {
                weekday: 'long',
                month: 'long',
                day: 'numeric'
            }
        ).toUpperCase();


    document.getElementById(
        'home-date'
    ).textContent =
        dateStr;


    const hour =
        now.getHours();


    let label;


    if (hour < 5)
        label = 'late night';

    else if (hour < 12)
        label = 'morning';

    else if (hour < 18)
        label = 'afternoon';

    else if (hour < 22)
        label = 'evening';

    else
        label = 'late night';


    document.getElementById(
        'home-time-of-day'
    ).textContent =
        label;
}


/* =========================================================
   NAVEGACIÓN
   ========================================================= */

function showSection(sectionId, navId) {

    document
        .querySelectorAll('.section')
        .forEach(
            s => s.classList.remove('active')
        );


    document
        .getElementById(sectionId)
        .classList.add('active');


    document
        .querySelectorAll('.sidebar button')
        .forEach(
            b => b.classList.remove('active')
        );


    if (navId)
        document
            .getElementById(navId)
            .classList.add('active');
}


document.getElementById(
    'nav-home'
).addEventListener(
    'click',
    () => {

        lastSection =
            'section-home';

        showSection(
            'section-home',
            'nav-home'
        );
    }
);


document.getElementById(
    'nav-search'
).addEventListener(
    'click',
    () => {

        lastSection =
            'section-search';

        showSection(
            'section-search',
            'nav-search'
        );
    }
);


document.getElementById(
    'nav-collection'
).addEventListener(
    'click',
    () => {

        lastSection =
            'section-collection';

        showSection(
            'section-collection',
            'nav-collection'
        );
    }
);


document.getElementById(
    'nav-liked'
).addEventListener(
    'click',
    () => {

        lastSection =
            'section-liked';

        showSection(
            'section-liked',
            'nav-liked'
        );

        loadLikedSongs();
    }
);


document.getElementById(
    'recent-see-all'
).addEventListener(
    'click',
    () =>
        document
            .getElementById('nav-collection')
            .click()
);


document.getElementById(
    'albums-see-all'
).addEventListener(
    'click',
    () =>
        document
            .getElementById('nav-collection')
            .click()
);


document.getElementById(
    'playlists-see-all'
).addEventListener(
    'click',
    () =>
        document
            .getElementById('nav-collection')
            .click()
);


/* =========================================================
   BUSQUEDA HOME
   ========================================================= */

document.getElementById(
    'home-search-input'
).addEventListener(
    'keydown',
    e => {

        if (e.key !== 'Enter')
            return;


        const query =
            e.target.value.trim();


        if (!query)
            return;


        document
            .getElementById('nav-search')
            .click();


        const searchInput =
            document.getElementById(
                'search-input'
            );


        searchInput.value =
            query;


        runSearch(query);
    }
);


/* =========================================================
   HOME CARDS
   ========================================================= */

/*
   MARCO — imagen real (marco_canciones.png).

   Ya no se genera un SVG a mano: se usa el mismo asset
   dibujado que usan frame-song / marco_cancion_sonando,
   como una segunda <img> encima de la portada.
*/

const FRAME_ROTATIONS = [
    'scribble-rot-1',
    'scribble-rot-2',
    'scribble-rot-3',
    'scribble-rot-4'
];

let frameRotIndex = 0;

function buildCoverFrame() {

    const rotClass =
        FRAME_ROTATIONS[frameRotIndex % FRAME_ROTATIONS.length];

    frameRotIndex++;

    const frame =
        document.createElement('img');

    frame.className = 'cover-frame';
    frame.alt = '';

    frame.src =
        '/player/skins/imagenes/life/marco_canciones.png';

    return { frame, rotClass };
}


function renderHomeCards(
    containerId,
    items,
    mapFn
) {

    const container =
        document.getElementById(containerId);


    container.innerHTML = '';


    items.forEach(item => {

        const mapped =
            mapFn(item);


        if (!mapped)
            return;


        const card =
            document.createElement('div');


        card.className =
            'home-card';


        /* -----------------------------------------
           CONTENEDOR DE LA FOTOGRAFÍA
           ----------------------------------------- */

        const imgWrap =
            document.createElement('div');


        const { frame, rotClass } =
            buildCoverFrame();


        imgWrap.className =
            `framed-cover ${rotClass}`;


        /* -----------------------------------------
           PORTADA DE SPOTIFY
           ----------------------------------------- */

        const img =
            document.createElement('img');


        img.className =
            'home-card-img';


        img.src =
            mapped.image ?? '';


        img.alt =
            mapped.title ?? '';


        imgWrap.appendChild(img);


        /* -----------------------------------------
           MARCO (imagen encima de la portada)
           ----------------------------------------- */

        imgWrap.appendChild(frame);


        /* -----------------------------------------
           TEXTO
           ----------------------------------------- */

        const title =
            document.createElement('span');


        title.className =
            'home-card-title';


        title.textContent =
            mapped.title;


        const sub =
            document.createElement('span');


        sub.className =
            'home-card-sub';


        sub.textContent =
            mapped.subtitle ?? '';


        /* -----------------------------------------
           TARJETA
           ----------------------------------------- */

        card.appendChild(imgWrap);

        card.appendChild(title);

        card.appendChild(sub);


        card.addEventListener(
            'click',
            mapped.onClick
        );


        container.appendChild(card);
    });
}


/* =========================================================
   LISTAS
   ========================================================= */

function renderItemRow(
    container,
    imageUrl,
    label,
    onClick,
    meta = '',
    actionLabel = 'Añadir'
) {

    const li =
        document.createElement('li');


    li.className =
        'item-row';


    const card =
        document.createElement('div');

    card.className = 'item-polaroid';


    const img =
        document.createElement('img');


    img.src =
        imageUrl ?? '';

    img.alt =
        label ?? '';

    const title =
        document.createElement('div');

    title.className =
        'polaroid-title';

    title.textContent =
        label ?? '';

    const artist =
        document.createElement('div');

    artist.className =
        'polaroid-artist';

    artist.textContent =
        meta;

    const info =
        document.createElement('div');

    info.className =
        'polaroid-info';

    info.appendChild(title);
    info.appendChild(artist);


    card.appendChild(img);
    card.appendChild(info);

    const action = document.createElement('button');
    action.className = 'search-result-action';
    action.type = 'button';
    action.title = actionLabel;
    action.setAttribute('aria-label', actionLabel);
    action.textContent = actionLabel === 'Reproducir' ? '▶' : '+';
    action.addEventListener('click', event => {
        event.stopPropagation();
        onClick();
    });
    card.appendChild(action);

    li.appendChild(card);


    li.addEventListener(
        'click',
        onClick
    );


    container.appendChild(li);
}


/* =========================================================
   CARGA HOME
   ========================================================= */

async function loadHomeData() {

    /* -----------------------------------------
       RECENTLY PLAYED
       ----------------------------------------- */

    try {

        const res =
            await fetch(
                '/api/player/recently-played'
            );


        if (!res.ok)
            throw new Error(
                `HTTP ${res.status}`
            );


        const data =
            await res.json();


        const seen =
            new Set();


        const uniqueTracks =
            [];


        data.items.forEach(item => {

            const track =
                item.track;


            if (
                !track ||
                seen.has(track.id)
            )
                return;


            seen.add(track.id);

            uniqueTracks.push(track);
        });


        renderHomeCards(
            'home-recent-cards',
            uniqueTracks,

            track => ({

                image:
                    track.album?.images?.[0]?.url,

                title:
                    track.name,

                subtitle:
                    track.artists
                        .map(a => a.name)
                        .join(', '),

                onClick:
                    () =>
                        playSingleTrack(
                            track.uri
                        )
            })
        );


    } catch (error) {

        console.error(
            'Error al cargar recientes (home):',
            error
        );
    }


    /* -----------------------------------------
       ALBUMS
       ----------------------------------------- */

    try {

        const res =
            await fetch(
                '/api/player/albums'
            );


        if (!res.ok)
            throw new Error(
                `HTTP ${res.status}`
            );


        const data =
            await res.json();


        renderHomeCards(
            'home-albums-cards',
            data.items,

            item => ({

                image:
                    item.album.images?.[0]?.url,

                title:
                    item.album.name,

                subtitle:
                    item.album.artists
                        .map(a => a.name)
                        .join(', '),

                onClick:
                    () =>
                        playContext(
                            item.album.uri
                        )
            })
        );


    } catch (error) {

        console.error(
            'Error al cargar albumes (home):',
            error
        );
    }


    /* -----------------------------------------
       PLAYLISTS
       ----------------------------------------- */

    try {

        const res =
            await fetch(
                '/api/player/playlists'
            );


        if (!res.ok)
            throw new Error(
                `HTTP ${res.status}`
            );


        const data =
            await res.json();


        renderHomeCards(
            'home-playlists-cards',
            data.items,

            playlist => ({

                image:
                    playlist.images?.[0]?.url,

                title:
                    playlist.name ||
                    '(sin nombre)',

                subtitle:
                    `${playlist.items?.total ?? playlist.tracks?.total ?? 0} songs`,

                onClick:
                    () =>
                        showTracklist(playlist.id, playlist.uri, playlist.name, 'playlist', playlist.images?.[0]?.url, playlist.owner?.display_name, playlist.items?.total ?? playlist.tracks?.total)
            })
        );


    } catch (error) {

        console.error(
            'Error al cargar playlists (home):',
            error
        );
    }
}


/* =========================================================
   REPRODUCCIÓN
   ========================================================= */

async function playSingleTrack(
    trackUri
) {

    try {

        const res =
            await fetch(
                `/api/player/play-single?trackUri=${encodeURIComponent(trackUri)}`,
                {
                    method: 'PUT'
                }
            );


        if (!res.ok)
            throw new Error(
                `HTTP ${res.status}`
            );


    } catch (error) {

        console.error(
            'Error al reproducir cancion:',
            error
        );


        statusEl.textContent =
            'Error: No se pudo reproducir la cancion';
    }
}


async function playContext(uri) {

    try {

        const response = await fetch(
                `/api/player/play-context?uri=${encodeURIComponent(uri)}&deviceId=${encodeURIComponent(deviceId ?? '')}`,
                {
                    method: 'PUT'
                }
            );


        if (!response.ok)
            throw new Error(
                `HTTP ${response.status}`
            );


    } catch (error) {

        console.error(
            'Error al reproducir:',
            error
        );


        statusEl.textContent =
            'Error: No se pudo reproducir';
    }
}


/* =========================================================
   SEARCH
   ========================================================= */

let searchDebounce;


document.getElementById(
    'search-input'
).addEventListener(
    'input',
    e => {

        clearTimeout(
            searchDebounce
        );


        const query =
            e.target.value.trim();


        if (query.length < 2) {

            document.getElementById(
                'search-results'
            ).innerHTML = '';

            return;
        }


        searchDebounce =
            setTimeout(
                () => runSearch(query),
                400
            );
    }
);

document.querySelector('.search-page-shell b').addEventListener('click', () => {
    const searchInput = document.getElementById('search-input');
    searchInput.value = '';
    document.getElementById('search-results').innerHTML = '';
    searchInput.focus();
});

document.querySelectorAll('.search-filters button').forEach(button => {
    button.addEventListener('click', () => {
        document.querySelectorAll('.search-filters button').forEach(item => item.classList.remove('active'));
        button.classList.add('active');
        const query = document.getElementById('search-input').value.trim();
        if (query.length >= 2) runSearch(query);
    });
});

function searchFilterAllows(type) {
    const active = document.querySelector('.search-filters button.active')?.textContent.trim().toLowerCase();
    return active === 'all' || active === type;
}


async function runSearch(query) {

    try {

        const res =
            await fetch(
                `/api/player/search?q=${encodeURIComponent(query)}`
            );


        if (!res.ok)
            throw new Error(
                `HTTP ${res.status}`
            );


        const data =
            await res.json();


        const results =
            document.getElementById(
                'search-results'
            );


        results.innerHTML = '';

        if (!data.tracks?.items?.length && !data.albums?.items?.length && !data.artists?.items?.length && !data.playlists?.items?.length) {
            results.innerHTML = '<p class="search-empty">No encontramos resultados para esa búsqueda.</p>';
            return;
        }


        /* CANCIONES */

        if (searchFilterAllows('songs') &&
            data.tracks?.items?.length
        ) {

            const h =
                document.createElement('h4');


            h.textContent =
                'Canciones';


            results.appendChild(h);


            const ul =
                document.createElement('ul');


            ul.className =
                'songs-grid';


            ul.style.cssText =
                'list-style:none; padding:0;';


            data.tracks.items.forEach(
                track => {

                    renderItemRow(
                        ul,

                        track.album
                            ?.images?.[0]?.url,

                        track.name,

                        () =>
                            playSingleTrack(
                                track.uri
                            ),
                        `Canción · ${track.artists.map(a => a.name).join(', ')}`,
                        'Reproducir'
                    );
                }
            );


            results.appendChild(ul);
        }


        /* ALBUMES */

        if (searchFilterAllows('albums') &&
            data.albums?.items?.length
        ) {

            const h =
                document.createElement('h4');


            h.textContent =
                'Álbumes';


            results.appendChild(h);


            const ul =
                document.createElement('ul');


            ul.className =
                'albums-grid';


            ul.style.cssText =
                'list-style:none; padding:0;';


            data.albums.items.forEach(
                album => {

                    renderItemRow(
                        ul,

                        album.images
                            ?.[0]?.url,

                        album.name,

                        () =>
                            playContext(
                                album.uri
                            ),
                        `Álbum · ${album.artists.map(a => a.name).join(', ')}`,
                        'Reproducir'
                    );
                }
            );


            results.appendChild(ul);
        }


        /* ARTISTAS */

        if (searchFilterAllows('artists') && data.artists?.items?.length) {

            const h =
                document.createElement('h4');


            h.textContent =
                'Artistas';


            results.appendChild(h);


            const ul =
                document.createElement('ul');


            ul.className = 'search-result-list';


            ul.style.cssText =
                'list-style:none; padding:0;';


            data.artists.items.forEach(artist => {
                    renderItemRow(
                        ul,

                        artist.images
                            ?.[0]?.url,

                        artist.name,

                        () => searchArtistTopTracks(artist.name),
                        `Artista · ${artist.followers?.total?.toLocaleString('es-ES') ?? 0} seguidores`,
                        'Ver canciones'
                    );
            });


            results.appendChild(ul);
        }

        if (searchFilterAllows('playlists') && data.playlists?.items?.length) {
            const h = document.createElement('h4');
            h.textContent = 'Playlists';
            results.appendChild(h);

            const ul = document.createElement('ul');
            ul.className = 'search-result-list';

            data.playlists.items.forEach(playlist => {
                renderItemRow(
                    ul,
                    playlist.images?.[0]?.url,
                    playlist.name || '(sin nombre)',
                    () => showTracklist(playlist.id, playlist.uri, playlist.name, 'playlist', playlist.images?.[0]?.url, playlist.owner?.display_name, playlist.items?.total ?? playlist.tracks?.total),
                    `Playlist · ${playlist.items?.total ?? playlist.tracks?.total ?? 0} canciones`,
                    'Ver canciones'
                );
            });
            results.appendChild(ul);
        }


    } catch (error) {

        console.error(
            'Error en busqueda:',
            error
        );
    }
}

async function searchArtistTopTracks(artistName) {
    const input = document.getElementById('search-input');
    input.value = `artist:${artistName}`;
    await runSearch(input.value);
}


/* =========================================================
   COLLECTION
   ========================================================= */

document.getElementById(
    'tab-playlists'
).addEventListener(
    'click',
    () => {

        document.getElementById(
            'playlists-list'
        ).style.display =
            'block';


        document.getElementById(
            'albums-list'
        ).style.display =
            'none';


        document.getElementById(
            'tab-playlists'
        ).classList.add('active');


        document.getElementById(
            'tab-albums'
        ).classList.remove('active');
    }
);


document.getElementById(
    'tab-albums'
).addEventListener(
    'click',
    () => {

        document.getElementById(
            'playlists-list'
        ).style.display =
            'none';


        document.getElementById(
            'albums-list'
        ).style.display =
            'block';


        document.getElementById(
            'tab-albums'
        ).classList.add('active');


        document.getElementById(
            'tab-playlists'
        ).classList.remove('active');
    }
);


/* =========================================================
   PLAYLISTS
   ========================================================= */

async function loadPlaylists() {

    try {

        const items = [];
        let offset = 0;
        let data;

        do {
            const res = await fetch(`/api/player/playlists?offset=${offset}`);
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            data = await res.json();
            items.push(...(data.items ?? []));
            offset += data.limit ?? 50;
        } while (data.next);


        const list =
            document.getElementById(
                'playlists-list'
            );


        list.innerHTML = '';


        items.forEach(
            playlist => {

                renderItemRow(
                    list,

                    playlist.images
                        ?.[0]?.url,

                    playlist.name ||
                        '(sin nombre)',

                    () =>
                        showTracklist(playlist.id, playlist.uri, playlist.name, 'playlist', playlist.images?.[0]?.url, playlist.owner?.display_name, playlist.items?.total ?? playlist.tracks?.total),
                    `Playlist · ${playlist.items?.total ?? playlist.tracks?.total ?? 0} canciones`,
                    'Ver canciones'
                );
            }
        );


    } catch (error) {

        console.error(
            'Error al cargar playlists:',
            error
        );


        document.getElementById(
            'playlists-list'
        ).innerHTML =
            '<li style="color:red;">Error al cargar playlists</li>';
    }
}


/* =========================================================
   ALBUMES
   ========================================================= */

async function loadAlbums() {

    try {

        const res =
            await fetch(
                '/api/player/albums'
            );


        if (!res.ok)
            throw new Error(
                `HTTP ${res.status}`
            );


        const data =
            await res.json();


        const list =
            document.getElementById(
                'albums-list'
            );


        list.innerHTML = '';


        data.items.forEach(
            item => {

                const album =
                    item.album;


                renderItemRow(
                    list,

                    album.images
                        ?.[0]?.url,

                    `${album.name} — ${album.artists
                        .map(a => a.name)
                        .join(', ')}`,

                    () =>
                        showTracklist(
                            album.id,
                            album.uri,
                            album.name,
                            'album'
                        ),
                    `Álbum · ${album.artists.map(a => a.name).join(', ')}`,
                    'Ver canciones'
                );
            }
        );


    } catch (error) {

        console.error(
            'Error al cargar álbumes:',
            error
        );


        document.getElementById(
            'albums-list'
        ).innerHTML =
            '<li style="color:red;">Error al cargar álbumes</li>';
    }
}


/* =========================================================
   LIKED SONGS
   ========================================================= */

async function loadLikedSongs() {

    try {

        const res =
            await fetch(
                '/api/player/liked-songs'
            );


        if (!res.ok)
            throw new Error(
                `HTTP ${res.status}`
            );


        const data =
            await res.json();


        const list =
            document.getElementById(
                'liked-list'
            );


        list.innerHTML = '';


        data.items.forEach(
            item => {

                const track =
                    item.track;


                if (!track)
                    return;


                renderItemRow(
                    list,

                    track.album
                        ?.images?.[0]?.url,

                    track.name,

                    () =>
                        playSingleTrack(
                            track.uri
                        ),
                    `Canción · ${track.artists.map(a => a.name).join(', ')}`,
                    'Reproducir'
                );
            }
        );


    } catch (error) {

        console.error(
            'Error al cargar liked songs:',
            error
        );


        document.getElementById(
            'liked-list'
        ).innerHTML =
            '<li style="color:red;">Error al cargar tus canciones</li>';
    }
}


/* =========================================================
   TRACKLIST
   ========================================================= */

async function showTracklist(
    collectionId,
    collectionUri,
    collectionName,
    collectionType = 'playlist',
    collectionImage = '',
    collectionOwner = '',
    collectionTotal = null
) {

    try {

        const endpoint = collectionType === 'album' ? 'album-tracks?albumId=' : 'playlist-tracks?playlistId=';
        const items = [];
        let offset = 0;
        let data;

        do {
            const res = await fetch(`/api/player/${endpoint}${collectionId}&offset=${offset}`);
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            data = await res.json();
            items.push(...(data.items ?? []));
            offset += data.limit ?? 50;
        } while (data.next);


        document.getElementById(
            'tracklist-title'
        ).textContent =
            collectionName;

        document.getElementById('tracklist-cover').src = collectionImage || '';
        document.getElementById('tracklist-owner').textContent = collectionOwner || 'Tu colección en Spotify';
        document.getElementById('tracklist-count').textContent = `${collectionTotal ?? items.length} canciones`;
        document.getElementById('playlist-play-all').onclick = () => playContext(collectionUri, deviceId);
        document.getElementById('playlist-shuffle').onclick = async () => {
            await playContext(collectionUri, deviceId);
            await player?.toggleShuffle({ state: true });
        };


        const trackList =
            document.getElementById(
                'tracklist'
            );


        trackList.innerHTML = '';

        if (!items.length) {
            trackList.innerHTML = '<li class="tracklist-empty">No hay canciones disponibles en esta colección.</li>';
        }


        items.forEach(
            (item, index) => {

                const track =
                    item.track ?? item.item ?? item;


                if (!track)
                    return;


                renderItemRow(
                    trackList,

                    track.album
                        ?.images?.[0]?.url,

                    track.name,

                    () =>
                        playTrackInPlaylist(
                            collectionUri,
                            track.uri
                        ),
                    `Canción · ${track.artists.map(a => a.name).join(', ')}`,
                    'Reproducir'
                );

                const row = trackList.lastElementChild?.querySelector('.item-polaroid');
                if (row) {
                    const number = document.createElement('span');
                    number.className = 'playlist-track-number';
                    number.textContent = String(index + 1);
                    row.prepend(number);
                    row.style.setProperty('--track-album', `'${track.album?.name ?? ''}'`);
                    row.dataset.duration = formatTime(track.duration_ms ?? 0);
                }
            }
        );


        showSection(
            'section-tracklist',
            null
        );


    } catch (error) {

        console.error(
            'Error al cargar tracklist:',
            error
        );

        document.getElementById('tracklist').innerHTML =
            '<li class="tracklist-empty">No se pudieron cargar las canciones. Inténtalo de nuevo.</li>';
    }
}


async function playTrackInPlaylist(
    contextUri,
    trackUri
) {

    try {

        const res =
            await fetch(
                `/api/player/play-track?contextUri=${encodeURIComponent(contextUri)}&trackUri=${encodeURIComponent(trackUri)}`,
                {
                    method: 'PUT'
                }
            );


        if (!res.ok)
            throw new Error(
                `HTTP ${res.status}`
            );


    } catch (error) {

        console.error(
            'Error al reproducir cancion:',
            error
        );


        statusEl.textContent =
            'Error: No se pudo reproducir la cancion';
    }
}


/* =========================================================
   VOLVER
   ========================================================= */

document.getElementById(
    'back-to-collection'
).addEventListener(
    'click',
    () => {

        const navMap = {

            'section-home':
                'nav-home',

            'section-search':
                'nav-search',

            'section-collection':
                'nav-collection',

            'section-liked':
                'nav-liked'
        };


        showSection(
            lastSection,
            navMap[lastSection]
        );
    }
);


/* =========================================================
   CARGA INICIAL
   ========================================================= */

document.addEventListener(
    'DOMContentLoaded',
    () => {

        renderHomeDate();

        loadCurrentTrack();

        loadHomeData();

        loadPlaylists();

        loadAlbums();

        loadAccountProfile();
    }
);