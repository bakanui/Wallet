<?php

require_once 'include/DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['uid']) && isset($_POST['amount'])) {

    // receiving the post params
    $unique_id = $_POST['uid'];
    $description = $_POST['description'];
    $amount = $_POST['amount'];

    // create a new transaction
    $transaction = $db->storeTransaction($unique_id, $description, $amount);
    if ($transaction) {
        // transaction stored successfully
        $response["error"] = FALSE;
        $response["uid"] = $transaction["unique_id"];
        $response["transaction"]["description"] = $transaction["description"];
        $response["transaction"]["amount"] = $transaction["amount"];
        $response["transaction"]["created_at"] = $transaction["created_at"];
        echo json_encode($response);
    } else {
        // transaction failed to store
        $response["error"] = TRUE;
        $response["error_msg"] = "Unknown error occurred in registration!";
        echo json_encode($response);
    }

} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters (unique_id, description, or amount) is missing!";
    echo json_encode($response);
}
?>