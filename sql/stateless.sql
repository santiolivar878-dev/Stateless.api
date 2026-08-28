-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 27-08-2026 a las 02:15:54
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `stateless`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cache`
--

CREATE TABLE `cache` (
  `key` varchar(255) NOT NULL,
  `value` mediumtext NOT NULL,
  `expiration` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `cache`
--

INSERT INTO `cache` (`key`, `value`, `expiration`) VALUES
('laravel-cache-0ade7c2cf97f75d009975f4d720d1fa6c19f4897', 'i:2;', 1781925967),
('laravel-cache-0ade7c2cf97f75d009975f4d720d1fa6c19f4897:timer', 'i:1781925967;', 1781925967),
('laravel-cache-17ba0791499db908433b80f37c5fbc89b870084b', 'i:3;', 1782949442),
('laravel-cache-17ba0791499db908433b80f37c5fbc89b870084b:timer', 'i:1782949442;', 1782949442),
('laravel-cache-356a192b7913b04c54574d18c28d46e6395428ab', 'i:4;', 1782949310),
('laravel-cache-356a192b7913b04c54574d18c28d46e6395428ab:timer', 'i:1782949310;', 1782949310),
('laravel-cache-da4b9237bacccdf19c0760cab7aec4a8359010b0', 'i:2;', 1781981963),
('laravel-cache-da4b9237bacccdf19c0760cab7aec4a8359010b0:timer', 'i:1781981963;', 1781981963);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cache_locks`
--

CREATE TABLE `cache_locks` (
  `key` varchar(255) NOT NULL,
  `owner` varchar(255) NOT NULL,
  `expiration` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `carritos`
--

CREATE TABLE `carritos` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `carritos`
--

INSERT INTO `carritos` (`id`, `user_id`, `created_at`, `updated_at`) VALUES
(1, 1, '2026-06-11 02:54:56', '2026-06-11 02:54:56');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `carrito_items`
--

CREATE TABLE `carrito_items` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `carrito_id` bigint(20) UNSIGNED NOT NULL,
  `producto_id` bigint(20) UNSIGNED NOT NULL,
  `cantidad` int(11) NOT NULL DEFAULT 1,
  `precio_unitario` decimal(38,2) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `variante_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categorias`
--

CREATE TABLE `categorias` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `categorias`
--

INSERT INTO `categorias` (`id`, `nombre`, `descripcion`, `estado`, `created_at`, `updated_at`) VALUES
(1, 'Essentials', 'na', 1, '2026-06-11 02:56:25', '2026-06-11 02:56:25'),
(2, 'octane', NULL, 1, '2026-06-21 01:41:52', '2026-06-21 01:41:52'),
(3, 'Waves', NULL, 1, '2026-06-21 23:48:36', '2026-06-21 23:56:02');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `envios`
--

CREATE TABLE `envios` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `venta_id` bigint(20) UNSIGNED NOT NULL,
  `fecha_envio` timestamp NULL DEFAULT NULL,
  `direccion` varchar(255) NOT NULL,
  `ciudad` varchar(255) NOT NULL,
  `estado` varchar(255) NOT NULL DEFAULT 'pendiente',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `fecha_confirmado` datetime(6) DEFAULT NULL,
  `fecha_en_curso` datetime(6) DEFAULT NULL,
  `fecha_entregado` datetime(6) DEFAULT NULL,
  `fecha_preparando` datetime(6) DEFAULT NULL,
  `numero_guia` varchar(255) DEFAULT NULL,
  `transportadora` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `envios`
--

INSERT INTO `envios` (`id`, `venta_id`, `fecha_envio`, `direccion`, `ciudad`, `estado`, `created_at`, `updated_at`, `fecha_confirmado`, `fecha_en_curso`, `fecha_entregado`, `fecha_preparando`, `numero_guia`, `transportadora`) VALUES
(1, 1, NULL, 'calle 68b #111-b', 'bogota', 'en_curso', '2026-06-11 02:57:45', '2026-06-20 05:48:06', NULL, '2026-08-26 03:09:08.000000', NULL, NULL, '548486848654', 'DHL'),
(2, 2, NULL, 'calle 68b #111-b', 'bogota', 'entregado', '2026-06-11 03:24:00', '2026-06-29 01:32:44', NULL, NULL, NULL, NULL, NULL, NULL),
(3, 3, NULL, 'calle 68b #111-b', 'bogota', 'entregado', '2026-06-11 03:31:42', '2026-06-29 01:32:46', NULL, NULL, NULL, NULL, NULL, NULL),
(4, 4, NULL, 'calle 68b #111-b', 'bogota', 'entregado', '2026-06-11 03:32:31', '2026-06-29 01:32:49', NULL, NULL, NULL, NULL, NULL, NULL),
(5, 5, NULL, 'calle 68b #111-b', 'bogota', 'pendiente', '2026-06-11 03:33:12', '2026-06-29 01:33:56', NULL, NULL, NULL, NULL, NULL, NULL),
(6, 6, NULL, 'calle 68b #111-b', 'bogota', 'en_curso', '2026-06-11 03:33:37', '2026-06-29 01:33:58', NULL, NULL, NULL, NULL, NULL, NULL),
(7, 7, NULL, 'calle 68b #111-b', 'bogota', 'entregado', '2026-06-11 03:46:27', '2026-06-29 01:33:20', NULL, NULL, NULL, NULL, NULL, NULL),
(8, 8, NULL, 'calle 68b #111-b', 'bogota', 'entregado', '2026-06-11 03:46:56', '2026-06-29 01:33:22', NULL, NULL, NULL, NULL, NULL, NULL),
(9, 9, NULL, 'calle 68b #111-b', 'bogota', 'entregado', '2026-06-11 03:47:24', '2026-06-29 01:33:26', NULL, NULL, NULL, NULL, NULL, NULL),
(10, 10, NULL, 'calle 68b #111-b', 'bogota', 'entregado', '2026-06-11 04:01:44', '2026-06-29 01:33:28', NULL, NULL, NULL, NULL, NULL, NULL),
(11, 11, NULL, 'aca', 'bogota', 'entregado', '2026-06-11 04:03:29', '2026-06-29 01:33:31', NULL, NULL, NULL, NULL, NULL, NULL),
(12, 12, NULL, 'aca', 'bogota', 'entregado', '2026-06-21 01:47:56', '2026-06-29 01:33:33', NULL, NULL, NULL, NULL, NULL, NULL),
(13, 13, NULL, 'calle 68b #111-b', 'bogota', 'entregado', '2026-06-26 20:36:16', '2026-06-29 01:33:18', NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `failed_jobs`
--

CREATE TABLE `failed_jobs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `connection` text NOT NULL,
  `queue` text NOT NULL,
  `payload` longtext NOT NULL,
  `exception` longtext NOT NULL,
  `failed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `jobs`
--

CREATE TABLE `jobs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `queue` varchar(255) NOT NULL,
  `payload` longtext NOT NULL,
  `attempts` tinyint(3) UNSIGNED NOT NULL,
  `reserved_at` int(10) UNSIGNED DEFAULT NULL,
  `available_at` int(10) UNSIGNED NOT NULL,
  `created_at` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `job_batches`
--

CREATE TABLE `job_batches` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `total_jobs` int(11) NOT NULL,
  `pending_jobs` int(11) NOT NULL,
  `failed_jobs` int(11) NOT NULL,
  `failed_job_ids` longtext NOT NULL,
  `options` mediumtext DEFAULT NULL,
  `cancelled_at` int(11) DEFAULT NULL,
  `created_at` int(11) NOT NULL,
  `finished_at` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `migrations`
--

CREATE TABLE `migrations` (
  `id` int(10) UNSIGNED NOT NULL,
  `migration` varchar(255) NOT NULL,
  `batch` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `migrations`
--

INSERT INTO `migrations` (`id`, `migration`, `batch`) VALUES
(1, '0000_01_01_000000_create_roles_table', 1),
(2, '0001_01_01_000000_create_users_table', 1),
(3, '0001_01_01_000001_create_cache_table', 1),
(4, '0001_01_01_000002_create_jobs_table', 1),
(5, '2026_05_25_203620_create_categorias_table', 1),
(6, '2026_05_25_203621_create_proveedors_table', 1),
(7, '2026_05_25_203622_create_productos_table', 1),
(8, '2026_05_25_203624_create_ventas_table', 1),
(9, '2026_05_25_203625_create_envios_table', 1),
(10, '2026_06_03_002955_create_carritos_table', 1),
(11, '2026_06_03_002959_create_carrito_items_table', 1),
(12, '2026_06_09_204536_create_producto_imagens_table', 1),
(13, '2026_06_10_223701_add_codigo_pago_to_ventas_table', 2),
(14, '2026_06_11_205853_add_verification_to_users_table', 3),
(15, '2026_06_16_185055_create_personal_access_tokens_table', 4),
(16, '2026_06_22_024838_create_producto_variantes_table', 5),
(17, '2026_06_26_182034_add_estado_to_ventas_table', 6),
(18, '2026_06_26_201927_create_venta_items_table', 6),
(19, '2026_06_28_201125_add_estado_to_users_table', 6);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `password_reset_tokens`
--

CREATE TABLE `password_reset_tokens` (
  `email` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `expiry_date` datetime(6) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `personal_access_tokens`
--

CREATE TABLE `personal_access_tokens` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `tokenable_type` varchar(255) NOT NULL,
  `tokenable_id` bigint(20) UNSIGNED NOT NULL,
  `name` text NOT NULL,
  `token` varchar(64) NOT NULL,
  `abilities` text DEFAULT NULL,
  `last_used_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

CREATE TABLE `productos` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `precio` decimal(38,2) DEFAULT NULL,
  `estado` varchar(255) NOT NULL DEFAULT 'activo',
  `imagen` varchar(255) DEFAULT NULL,
  `stock_actual` int(11) NOT NULL DEFAULT 0,
  `stock_minimo` int(11) NOT NULL DEFAULT 5,
  `stock_maximo` int(11) NOT NULL DEFAULT 100,
  `categoria_id` bigint(20) UNSIGNED NOT NULL,
  `proveedor_id` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `productos`
--

INSERT INTO `productos` (`id`, `nombre`, `descripcion`, `precio`, `estado`, `imagen`, `stock_actual`, `stock_minimo`, `stock_maximo`, `categoria_id`, `proveedor_id`, `created_at`, `updated_at`) VALUES
(1, 'pantalo negro', 'asas', 250.00, 'activo', 'Cargo_Oversize_negro_esencial.jpg', 37, 5, 100, 1, 1, '2026-06-11 02:56:59', '2026-06-26 20:36:16'),
(2, 'pantalon oc', 'asasasas', 250000.00, 'activo', 'pantalon_sonido_negro_atras.png', 11, 5, 100, 1, 1, '2026-06-21 01:42:21', '2026-06-26 20:36:16'),
(3, 'camisa octane', 'camisaaaa', 150000.00, 'activo', 'saturado_atras_WAVES.png', 156, 5, 100, 1, 1, '2026-06-21 01:52:34', '2026-06-21 02:22:07'),
(4, 'lalanta', 'alsfkhrgr', 56489.00, 'activo', 'onda_atras_WAVES.png', 159, 5, 100, 1, 1, '2026-06-21 02:27:34', '2026-06-21 02:27:34'),
(6, 'camisa octane', 'octane camisa', 150.00, 'activo', 'Camiseta_Arakiri_frente_OCTANE.png', 159, 5, 100, 2, 1, '2026-06-21 23:29:30', '2026-06-21 23:29:30'),
(7, 'dispersa', NULL, 25690.00, 'activo', 'dispersa_negra_atras_WAVES.png', 250, 5, 100, 3, 1, '2026-06-21 23:56:54', '2026-06-21 23:56:54'),
(8, 'nuevo ejemplo', 'ejemplo', 159000.00, 'activo', 'camisa_overzice_negra_ESENCIAL.jpg', 147, 5, 100, 1, 1, '2026-06-22 07:59:30', '2026-06-22 07:59:30'),
(9, 'camisaaaaaa', 'pantlaon', 250000000.00, 'activo', 'https://res.cloudinary.com/cdctbogi/image/upload/v1787713677/stateless_products/iuygm2z4huwayriemcmc.webp', 152, 5, 100, 1, 1, NULL, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto_imagenes`
--

CREATE TABLE `producto_imagenes` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `producto_id` bigint(20) UNSIGNED NOT NULL,
  `imagen` varchar(255) NOT NULL,
  `orden` int(11) NOT NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto_variantes`
--

CREATE TABLE `producto_variantes` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `producto_id` bigint(20) UNSIGNED NOT NULL,
  `color` varchar(255) NOT NULL,
  `hex` varchar(255) DEFAULT NULL,
  `imagen` varchar(255) DEFAULT NULL,
  `stock_actual` int(11) NOT NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `producto_variantes`
--

INSERT INTO `producto_variantes` (`id`, `producto_id`, `color`, `hex`, `imagen`, `stock_actual`, `created_at`, `updated_at`) VALUES
(4, 1, 'Blanco', '#ff0000', 'Fondo_1_WAVES.png', 50, '2026-06-22 08:14:55', '2026-06-22 08:14:55');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `proveedores`
--

CREATE TABLE `proveedores` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `telefono` varchar(255) NOT NULL,
  `correo` varchar(255) NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `proveedores`
--

INSERT INTO `proveedores` (`id`, `nombre`, `telefono`, `correo`, `estado`, `created_at`, `updated_at`) VALUES
(1, 'SA', '3013813718', 'sasa@gmail.com', 1, '2026-06-11 02:55:36', '2026-06-11 02:55:36');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`id`, `name`, `created_at`, `updated_at`) VALUES
(1, 'admin', '2026-06-11 02:54:21', '2026-06-11 02:54:21'),
(2, 'empleado', '2026-06-11 02:54:21', '2026-06-11 02:54:21'),
(3, 'cliente', '2026-06-11 02:54:21', '2026-06-11 02:54:21');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sessions`
--

CREATE TABLE `sessions` (
  `id` varchar(255) NOT NULL,
  `user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` text DEFAULT NULL,
  `payload` longtext NOT NULL,
  `last_activity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `sessions`
--

INSERT INTO `sessions` (`id`, `user_id`, `ip_address`, `user_agent`, `payload`, `last_activity`) VALUES
('cia6frqftTTqLYT9rrSevoQ66ilhq87VejkCh4M5', NULL, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.134.0 Chrome/148.0.7778.280 Electron/42.8.1 Safari/537.36', 'YTozOntzOjY6Il90b2tlbiI7czo0MDoibkN0SkdLSTJ0SVN6Vm5yTXZpYkRLQ2VlSUVmR0w4bDNISDczd2VVOSI7czo5OiJfcHJldmlvdXMiO2E6Mjp7czozOiJ1cmwiO3M6MjE6Imh0dHA6Ly8xMjcuMC4wLjE6ODAwMCI7czo1OiJyb3V0ZSI7Tjt9czo2OiJfZmxhc2giO2E6Mjp7czozOiJvbGQiO2E6MDp7fXM6MzoibmV3IjthOjA6e319fQ==', 1787627515),
('g3GmE1EHBN5P1oGTMUkdrtGcHuzqclf1sHdBz8fn', NULL, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/151.0.0.0 Safari/537.36', 'YTozOntzOjY6Il90b2tlbiI7czo0MDoiTUZ2UzVFM2dpeGVGbEhSUDYyckg3bzRHZlExdEV6TVRLRExnV1RJdyI7czo5OiJfcHJldmlvdXMiO2E6Mjp7czozOiJ1cmwiO3M6MjE6Imh0dHA6Ly8xMjcuMC4wLjE6ODAwMCI7czo1OiJyb3V0ZSI7Tjt9czo2OiJfZmxhc2giO2E6Mjp7czozOiJvbGQiO2E6MDp7fXM6MzoibmV3IjthOjA6e319fQ==', 1787627516),
('IiCbR9hlhvyMbDqI3xiZ99cE2lgkqC5lFElwn1JP', 11, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36', 'YTo1OntzOjY6Il90b2tlbiI7czo0MDoic0V4YXV6VUFFZWR5TkthTGozRVRoZFB3VW12eEJCUzl1dVZpV2hISiI7czo2OiJfZmxhc2giO2E6Mjp7czozOiJvbGQiO2E6MDp7fXM6MzoibmV3IjthOjA6e319czo5OiJfcHJldmlvdXMiO2E6Mjp7czozOiJ1cmwiO3M6Mjk6Imh0dHA6Ly8xMjcuMC4wLjE6ODAwMC9hY2NvdW50IjtzOjU6InJvdXRlIjtzOjc6ImFjY291bnQiO31zOjUwOiJsb2dpbl93ZWJfNTliYTM2YWRkYzJiMmY5NDAxNTgwZjAxNGM3ZjU4ZWE0ZTMwOTg5ZCI7aToxMTtzOjM6InVybCI7YTowOnt9fQ==', 1782949429),
('o2i5pWn8k329Phop5Jxi1v4MI8F6Oro5FZXWQWZv', NULL, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Code/1.134.0 Chrome/148.0.7778.280 Electron/42.8.1 Safari/537.36', 'YTozOntzOjY6Il90b2tlbiI7czo0MDoiQTdoNU1jcXI4TUljQXdDUnhYbFlCVTR3RGFudnEyRGVQb2VURUthTiI7czo5OiJfcHJldmlvdXMiO2E6Mjp7czozOiJ1cmwiO3M6MjE6Imh0dHA6Ly8xMjcuMC4wLjE6ODAwMCI7czo1OiJyb3V0ZSI7Tjt9czo2OiJfZmxhc2giO2E6Mjp7czozOiJvbGQiO2E6MDp7fXM6MzoibmV3IjthOjA6e319fQ==', 1787627516),
('OMMHDENZn72f9SazBC4QCc6jvSlChe2tgfNdLbTL', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/151.0.0.0 Safari/537.36', 'YTo0OntzOjY6Il90b2tlbiI7czo0MDoiSGZoZE5NbFFHQkpPdVo4MjBxbGZ4M3lpMlB0Z3BQQjJCUnRXNktpeiI7czo5OiJfcHJldmlvdXMiO2E6Mjp7czozOiJ1cmwiO3M6MzA6Imh0dHA6Ly8xMjcuMC4wLjE6ODAwMC9yZXBvcnRlcyI7czo1OiJyb3V0ZSI7czoxNDoicmVwb3J0ZXMuaW5kZXgiO31zOjY6Il9mbGFzaCI7YToyOntzOjM6Im9sZCI7YTowOnt9czozOiJuZXciO2E6MDp7fX1zOjUwOiJsb2dpbl93ZWJfNTliYTM2YWRkYzJiMmY5NDAxNTgwZjAxNGM3ZjU4ZWE0ZTMwOTg5ZCI7aToxO30=', 1787712227),
('vEyFb3dICSrXhuu2V12mYZ7sFJVKVuhKmgpCR20t', 11, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:152.0) Gecko/20100101 Firefox/152.0', 'YTo1OntzOjY6Il90b2tlbiI7czo0MDoiaHB0ZU90MGg4alBvWkZXQTlhOENrRFI0QkpNaFlSMWlJeEpiWDFUYyI7czo5OiJfcHJldmlvdXMiO2E6Mjp7czozOiJ1cmwiO3M6Mjk6Imh0dHA6Ly8xMjcuMC4wLjE6ODAwMC9hY2NvdW50IjtzOjU6InJvdXRlIjtzOjc6ImFjY291bnQiO31zOjY6Il9mbGFzaCI7YToyOntzOjM6Im9sZCI7YTowOnt9czozOiJuZXciO2E6MDp7fX1zOjM6InVybCI7YTowOnt9czo1MDoibG9naW5fd2ViXzU5YmEzNmFkZGMyYjJmOTQwMTU4MGYwMTRjN2Y1OGVhNGUzMDk4OWQiO2k6MTE7fQ==', 1782949430);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `users`
--

CREATE TABLE `users` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `email_verified` tinyint(1) NOT NULL DEFAULT 0,
  `email_token` varchar(255) DEFAULT NULL,
  `email_token_expires_at` timestamp NULL DEFAULT NULL,
  `reset_token` varchar(255) DEFAULT NULL,
  `reset_token_expires_at` timestamp NULL DEFAULT NULL,
  `email_verified_at` timestamp NULL DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role_id` bigint(20) UNSIGNED DEFAULT NULL,
  `estado` varchar(255) NOT NULL DEFAULT 'activo',
  `remember_token` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `email_verified`, `email_token`, `email_token_expires_at`, `reset_token`, `reset_token_expires_at`, `email_verified_at`, `password`, `role_id`, `estado`, `remember_token`, `created_at`, `updated_at`) VALUES
(1, 'Admin', 'admin@example.com', 0, NULL, NULL, NULL, NULL, NULL, '$2y$12$vLuv2VvogRTxcRqK.Wy1Iu6oWGk2vwZr3.SyEOrwAKaPQM3UbqWdC', 1, 'activo', NULL, '2026-06-11 02:54:21', '2026-06-11 02:54:21'),
(2, 'santiago', 'santiolivar878@gmail.com', 0, NULL, NULL, NULL, NULL, '2026-06-20 23:58:36', '$2y$12$Env3Yz99U1Cww5kgUix83ehpO6eiX6MkT2x8emmrPOAZ.qCmZEIOW', 2, 'activo', NULL, '2026-06-13 23:39:38', '2026-06-29 03:51:41'),
(11, 'Juber S', 'jubersolivarg@juandelcorral.edu.co', 1, NULL, NULL, NULL, NULL, NULL, '$2y$12$DJaZqP27iYAh.0Ogly6BKeF.UgR5vmG9YtXinDSyUJGzMFMCRYlli', 3, 'activo', NULL, '2026-07-02 04:39:03', '2026-07-02 04:43:47');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ventas`
--

CREATE TABLE `ventas` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `tipo_venta` varchar(255) NOT NULL,
  `metodo_pago` varchar(255) NOT NULL,
  `total` decimal(38,2) DEFAULT NULL,
  `codigo_pago` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `estado` varchar(255) NOT NULL DEFAULT 'pendiente'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `ventas`
--

INSERT INTO `ventas` (`id`, `tipo_venta`, `metodo_pago`, `total`, `codigo_pago`, `user_id`, `created_at`, `updated_at`, `estado`) VALUES
(1, 'online', 'tarjeta', 24250.00, NULL, 1, '2026-06-11 02:57:45', '2026-06-29 01:33:46', 'enviado'),
(2, 'online', 'efecty', 250.00, NULL, 1, '2026-06-11 03:24:00', '2026-06-29 01:33:49', 'pago_confirmado'),
(3, 'online', 'tarjeta', 11250.00, NULL, 1, '2026-06-11 03:31:42', '2026-06-29 01:33:50', 'pago_confirmado'),
(4, 'online', 'nequi', 250.00, NULL, 1, '2026-06-11 03:32:31', '2026-06-29 01:33:52', 'pago_confirmado'),
(5, 'online', 'pse', 1000.00, NULL, 1, '2026-06-11 03:33:12', '2026-06-29 01:33:56', 'en_preparacion'),
(6, 'online', 'efecty', 8750.00, NULL, 1, '2026-06-11 03:33:37', '2026-06-29 01:33:58', 'enviado'),
(7, 'online', 'efecty', 3750.00, 'EFY-943600DD', 1, '2026-06-11 03:46:27', '2026-06-29 01:33:20', 'entregado'),
(8, 'online', 'nequi', 500.00, NULL, 1, '2026-06-11 03:46:56', '2026-06-29 01:33:22', 'entregado'),
(9, 'online', 'pse', 250.00, NULL, 1, '2026-06-11 03:47:24', '2026-06-29 01:33:26', 'entregado'),
(10, 'online', 'pse', 1750.00, NULL, 1, '2026-06-11 04:01:44', '2026-06-29 01:33:28', 'entregado'),
(11, 'online', 'pse', 250.00, NULL, 1, '2026-06-11 04:03:29', '2026-06-29 01:33:31', 'entregado'),
(12, 'online', 'efecty', 24750000.00, 'EFY-C7C6C6F7', 1, '2026-06-21 01:47:56', '2026-06-29 01:33:33', 'entregado'),
(13, 'online', 'tarjeta', 250250.00, NULL, 1, '2026-06-26 20:36:16', '2026-06-29 01:33:18', 'entregado');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `venta_items`
--

CREATE TABLE `venta_items` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `venta_id` bigint(20) UNSIGNED NOT NULL,
  `producto_id` bigint(20) UNSIGNED NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(38,2) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `variante_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `cache`
--
ALTER TABLE `cache`
  ADD PRIMARY KEY (`key`),
  ADD KEY `cache_expiration_index` (`expiration`);

--
-- Indices de la tabla `cache_locks`
--
ALTER TABLE `cache_locks`
  ADD PRIMARY KEY (`key`),
  ADD KEY `cache_locks_expiration_index` (`expiration`);

--
-- Indices de la tabla `carritos`
--
ALTER TABLE `carritos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `carritos_user_id_foreign` (`user_id`);

--
-- Indices de la tabla `carrito_items`
--
ALTER TABLE `carrito_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `carrito_items_carrito_id_foreign` (`carrito_id`),
  ADD KEY `carrito_items_producto_id_foreign` (`producto_id`);

--
-- Indices de la tabla `categorias`
--
ALTER TABLE `categorias`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `envios`
--
ALTER TABLE `envios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `envios_venta_id_foreign` (`venta_id`);

--
-- Indices de la tabla `failed_jobs`
--
ALTER TABLE `failed_jobs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `failed_jobs_uuid_unique` (`uuid`);

--
-- Indices de la tabla `jobs`
--
ALTER TABLE `jobs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `jobs_queue_index` (`queue`);

--
-- Indices de la tabla `job_batches`
--
ALTER TABLE `job_batches`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `migrations`
--
ALTER TABLE `migrations`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  ADD PRIMARY KEY (`email`),
  ADD UNIQUE KEY `UKla2ts67g4oh2sreayswhox1i6` (`user_id`);

--
-- Indices de la tabla `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `personal_access_tokens_token_unique` (`token`),
  ADD KEY `personal_access_tokens_tokenable_type_tokenable_id_index` (`tokenable_type`,`tokenable_id`),
  ADD KEY `personal_access_tokens_expires_at_index` (`expires_at`);

--
-- Indices de la tabla `productos`
--
ALTER TABLE `productos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `productos_categoria_id_foreign` (`categoria_id`),
  ADD KEY `productos_proveedor_id_foreign` (`proveedor_id`);

--
-- Indices de la tabla `producto_imagenes`
--
ALTER TABLE `producto_imagenes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `producto_imagenes_producto_id_foreign` (`producto_id`);

--
-- Indices de la tabla `producto_variantes`
--
ALTER TABLE `producto_variantes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `producto_variantes_producto_id_foreign` (`producto_id`);

--
-- Indices de la tabla `proveedores`
--
ALTER TABLE `proveedores`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `roles_name_unique` (`name`);

--
-- Indices de la tabla `sessions`
--
ALTER TABLE `sessions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `sessions_user_id_index` (`user_id`),
  ADD KEY `sessions_last_activity_index` (`last_activity`);

--
-- Indices de la tabla `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `users_email_unique` (`email`),
  ADD KEY `users_role_id_foreign` (`role_id`);

--
-- Indices de la tabla `ventas`
--
ALTER TABLE `ventas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `ventas_user_id_foreign` (`user_id`);

--
-- Indices de la tabla `venta_items`
--
ALTER TABLE `venta_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `venta_items_venta_id_foreign` (`venta_id`),
  ADD KEY `venta_items_producto_id_foreign` (`producto_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `carritos`
--
ALTER TABLE `carritos`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `carrito_items`
--
ALTER TABLE `carrito_items`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT de la tabla `categorias`
--
ALTER TABLE `categorias`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `envios`
--
ALTER TABLE `envios`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `failed_jobs`
--
ALTER TABLE `failed_jobs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `jobs`
--
ALTER TABLE `jobs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `migrations`
--
ALTER TABLE `migrations`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT de la tabla `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `productos`
--
ALTER TABLE `productos`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `producto_imagenes`
--
ALTER TABLE `producto_imagenes`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `producto_variantes`
--
ALTER TABLE `producto_variantes`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `proveedores`
--
ALTER TABLE `proveedores`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `ventas`
--
ALTER TABLE `ventas`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `venta_items`
--
ALTER TABLE `venta_items`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `carritos`
--
ALTER TABLE `carritos`
  ADD CONSTRAINT `carritos_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `carrito_items`
--
ALTER TABLE `carrito_items`
  ADD CONSTRAINT `carrito_items_carrito_id_foreign` FOREIGN KEY (`carrito_id`) REFERENCES `carritos` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `carrito_items_producto_id_foreign` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `envios`
--
ALTER TABLE `envios`
  ADD CONSTRAINT `envios_venta_id_foreign` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `productos`
--
ALTER TABLE `productos`
  ADD CONSTRAINT `productos_categoria_id_foreign` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `productos_proveedor_id_foreign` FOREIGN KEY (`proveedor_id`) REFERENCES `proveedores` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `producto_imagenes`
--
ALTER TABLE `producto_imagenes`
  ADD CONSTRAINT `producto_imagenes_producto_id_foreign` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `producto_variantes`
--
ALTER TABLE `producto_variantes`
  ADD CONSTRAINT `producto_variantes_producto_id_foreign` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_role_id_foreign` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `ventas`
--
ALTER TABLE `ventas`
  ADD CONSTRAINT `ventas_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `venta_items`
--
ALTER TABLE `venta_items`
  ADD CONSTRAINT `venta_items_producto_id_foreign` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `venta_items_venta_id_foreign` FOREIGN KEY (`venta_id`) REFERENCES `ventas` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
