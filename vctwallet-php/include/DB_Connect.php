<?php
/**
 * @author Ravi Tamada
 * @link http://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/ Complete tutorial
 */

class DB_Connect {
    private $conn; // bikin variabel

    // Connecting to database
    public function connect() {
        require_once 'include/Config.php'; //biar bisa manggil isi dari config.php
        
        // Connecting to mysql database
        $this->conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE); //
        
        // return database handler
        return $this->conn; // return connection kaya misalkan ngambil dari codingan yg lain nah disini lah dia di return
    }
}

?>
