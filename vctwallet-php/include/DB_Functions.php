<?php
class DB_Functions {

    private $conn;

    // constructor (jadi kaya misalnya manggil dari insert.php nah terus connect ke DB_connect.php 
	//terus dia munculin di insert.php) 
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
    } 

    // destructor (kaya misalkan mau ngeclose ya ke close aja jadi makanya dia di kosongin)
    function __destruct() {
        
    }

    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($name, $email, $password) {
        $uuid = uniqid('', true); //function buat bikin unique id di php terus true itu buat 23 char, kalo false 13 char
        $hash = $this->hashSSHA($password); //SSHA = salted secure hash algorithm (buat ngeencrypt pass)
        $encrypted_password = $hash["encrypted"]; // encrypted password (udah di encryprt)
        $salt = $hash["salt"]; // salt (tujuannya buat ngesecure)

        $stmt = $this->conn->prepare("INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, ?, NOW())"); //buat masuk2in ke tablenya, NOW waktu skrg, stmt itu nama variabel
        $stmt->bind_param("sssss", $uuid, $name, $email, $encrypted_password, $salt); // ini buat ngisi tiap paramaternya s itu string
        $result = $stmt->execute(); 
        $stmt->close(); // connectionnya di close

        // check for successful store (ini buat mastiin semua yg tadi di atas udah masuk apa blm tapi dia make email)
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");// dari select itu buat ngecek satu baris yaitu buat email data yg tadi udah masuk apa blm
            $stmt->bind_param("s", $email); //ini buat ngisi parameternya
            $stmt->execute(); // ini buat ngejalanin
            $user = $stmt->get_result()->fetch_assoc(); // ini munculin
            $stmt->close();

            return $user;
        } else {
            return false;
        }
    }
	 /**
     * Storing new transaction
     * returns newly created transaction details
     */
	public function storeTransaction($unique_id, $description, $amount){
		$inn = $this->conn->prepare("INSERT INTO transactions(unique_id, description, amount, created_at) VALUES (?, ?, ?, NOW())");
		$inn->bind_param("sss", $unique_id, $description, $amount);
		$execs = $inn->execute();
		$execs->close();

		if ($execs) {
            $inn = $this->conn->prepare("SELECT * FROM transactions WHERE unique_id = ?");
            $inn->bind_param("s", $unique_id);
            $inn->execute();
            $user = $inn->get_result()->fetch_assoc();
            $inn->close();

            return $transaction;
        } else {
            return false;
        }
	}

    /**
     * Get user by email and password
     */
    public function getUserByEmailAndPassword($email, $password) {
		$query = "select * from users WHERE email = ?";
        $stmt = $this->conn->prepare($query);

        $stmt->bind_param("s", $email);

        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            // verifying user password
            $salt = $user['salt'];
            $encrypted_password = $user['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct
                return $user;
            }
        } else {
            return NULL;
        }
    }

    /**
     * Check user is existed or not
     */
    public function isUserExisted($email) {
        $stmt = $this->conn->prepare("SELECT email from users WHERE email = ?");

        $stmt->bind_param("s", $email);

        $stmt->execute();

        $stmt->store_result();

        if ($stmt->num_rows > 0) { 
            // user existed 
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }

    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {

        $salt = sha1(rand()); //ngesecure random alfanumeric
        $salt = substr($salt, 0, 10); //ini ngebersihin kalo stringnya pass ada titik atau ada koma
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt); //
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }

    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {

        $hash = base64_encode(sha1($password . $salt, true) . $salt);

        return $hash;
    }
}
?>