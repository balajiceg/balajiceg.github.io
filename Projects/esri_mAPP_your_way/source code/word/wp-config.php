<?php
/**
 * The base configurations of the WordPress.
 *
 * This file has the following configurations: MySQL settings, Table Prefix,
 * Secret Keys, WordPress Language, and ABSPATH. You can find more information
 * by visiting {@link http://codex.wordpress.org/Editing_wp-config.php Editing
 * wp-config.php} Codex page. You can get the MySQL settings from your web host.
 *
 * This file is used by the wp-config.php creation script during the
 * installation. You don't have to use the web site, you can just copy this file
 * to "wp-config.php" and fill in the values.
 *
 * @package WordPress
 */

// ** MySQL settings - You can get this info from your web host ** //
/** The name of the database for WordPress */
define('DB_NAME', 'cl47-a-wordp-w4h');

/** MySQL database username */
define('DB_USER', 'cl47-a-wordp-w4h');

/** MySQL database password */
define('DB_PASSWORD', 'H6mnB^Uxw');

/** MySQL hostname */
define('DB_HOST', 'localhost');

/** Database Charset to use in creating database tables. */
define('DB_CHARSET', 'utf8');

/** The Database Collate type. Don't change this if in doubt. */
define('DB_COLLATE', '');

/**#@+
 * Authentication Unique Keys and Salts.
 *
 * Change these to different unique phrases!
 * You can generate these using the {@link https://api.wordpress.org/secret-key/1.1/salt/ WordPress.org secret-key service}
 * You can change these at any point in time to invalidate all existing cookies. This will force all users to have to log in again.
 *
 * @since 2.6.0
 */
define('AUTH_KEY',         'W8)WjTQWtZ^5PL809+XbNJ!K)gHJhC18c31HKpaUMC7hrW()ISBbB)O7eAI(OilZ');
define('SECURE_AUTH_KEY',  'R-bA/PmiQVcT26lIkLG0WqGqR/dE23Z-x17GkHMnk1Kt34kYm7sO)X7BQyi8^+)t');
define('LOGGED_IN_KEY',    'xWp3Qo=6o14cjsr9jVe8EIqpXo1RjmRA9N7Ej!oQG#pMSLkUtTRCQ5_-d)4hK8XR');
define('NONCE_KEY',        '_Ex7_3oEuOT_CKMWhP(UQBMz^YxFPsEmLf+8E#pHNve-5tJnEZJ6)LDUh7nD0/W)');
define('AUTH_SALT',        ')upg9V1(1o15RStfAoZRmHRxZdkuAWQzgGBmdmOpixn2YqRl/cU_1y13tXxLHTAA');
define('SECURE_AUTH_SALT', 'toxe3btel8LJB#DE(x=x4B77-gwts8Gtfqty78e2b1!Z9ol(yeE-h6Kd#5zgsW0I');
define('LOGGED_IN_SALT',   'woJ0w2bDmPr=GVNzy/EWt)k+a^wjs+qDf8zCwoSKr0_Le55LAtd+T5sjsn206/2k');
define('NONCE_SALT',       'NcaJ3ZZw(MgUu7wtefmpTw6=c02EWHguqsOO-q2SaktT3WazA)mi+1-Gu#6xS5jP');

/**#@-*/

/**
 * WordPress Database Table prefix.
 *
 * You can have multiple installations in one database if you give each a unique
 * prefix. Only numbers, letters, and underscores please!
 */
$table_prefix  = 'wp_';

/**
 * WordPress Localized Language, defaults to English.
 *
 * Change this to localize WordPress. A corresponding MO file for the chosen
 * language must be installed to wp-content/languages. For example, install
 * de.mo to wp-content/languages and set WPLANG to 'de' to enable German
 * language support.
 */
define('WPLANG', '');

/**
 * For developers: WordPress debugging mode.
 *
 * Change this to true to enable the display of notices during development.
 * It is strongly recommended that plugin and theme developers use WP_DEBUG
 * in their development environments.
 */
define('WP_DEBUG', false);

/**
 *  Change this to true to run multiple blogs on this installation.
 *  Then login as admin and go to Tools -> Network
 */
define('WP_ALLOW_MULTISITE', false);

/* That's all, stop editing! Happy blogging. */

/** Absolute path to the WordPress directory. */
if ( !defined('ABSPATH') )
	define('ABSPATH', dirname(__FILE__) . '/');

/** Sets up WordPress vars and included files. */
require_once(ABSPATH . 'wp-settings.php');

/* Destination directory for file streaming */
define('WP_TEMP_DIR', ABSPATH . 'wp-content/');

