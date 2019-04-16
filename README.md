### wallet
Small project for mobile wallet APIs.

The project is written in Java using Spring Boot and Spring MVC frameworks.
MySQL is used as data storage.<br>
This project provides APIs to manage your wallet. See https://github.com/harshal-shirude/wallet/blob/master/README.md#apis for more details.<br>
Minimum/Maximum amount allowed at any point in time in wallet can be configured through `wallet.min_balance` and `wallet.max_balance` properties in `application.properties` file.<br>
Default values are `-50000.00` and `100000.00` respectively.

### Database:
There are 2 tables in the database,
#### 1. Transactions (which stores all the transaction entries)
```
CREATE TABLE `transactions` (
  `id` varchar(21) NOT NULL,
  `status` enum('SUCCESS','CANCELLED','IN_PROGRESS','FAILED') NOT NULL,
  `type` enum('CREDIT','DEBIT') NOT NULL,
  `amount` double(8,2) NOT NULL DEFAULT '0.00',
  `summary` varchar(50) DEFAULT NULL,
  `wallet_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_wallet_id` (`wallet_id`),
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`wallet_id`) REFERENCES `wallet` (`id`)
);
```
#### 2. Wallet (stores all wallets)
```
CREATE TABLE `wallet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` enum('ACTIVATED','DEACTIVATED') NOT NULL,
  `balance` double(8,2) DEFAULT '0.00',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);
```

### APIs
Using below APIs you can CREDIT amount to or DEBIT amount from a wallet, check wallet balance and so on.<br>
Below is the list of APIs and it's description.
#### 1. CREDIT/DEBIT Transaction API:

API: /wallet/{walletId}/transaction<br>
Method: POST<br>
Content-type: application/json<br>
Body:<br>
CREDIT Transaction
```
{
	"amount": 1000.50,
	"type": "CREDIT"
}
```
DEBIT Transaction
```
{
	"amount": 1000.50,
	"type": "DEBIT"
}
```
Response:<br>
On successfull Crediting/Debiting amount,
```
HttpStatus = 200 (OK)
{
    "data": "24s2WItCbs16a261cca14",
    "status": "SUCCESS",
    "message": "Your transaction was successful"
}
```
If wallet does not exist,
```
HttpStatus = 400 (BAD_REQUEST)
{
    "status": "FAILED",
    "message": "Failed to process your request, wallet not found with id : '5', Because of null"
}
```
#### 2. CANCEL Transaction(Revert transaction) API:
Description: Transaction can be reverted by using this API.<br>
Reverting CREDIT transaction will debit credited amount from wallet and reverting DEBIT transaction will credit debited amount.<br>
API: /wallet/{walletId}/transaction/{transactionId}<br>
Method: DELETE<br>
Response:
```
HttpStatus = 200 (OK)
{
    "data": "24s2WItCbs16a261cca14",
    "status": "SUCCESS",
    "message": "Transaction cancelled successfully"
}
```
Request will return `400 (BAD_REQUEST)` if transaction doesn't belong to wallet or wallet/transaction is invalid.
```
{
    "data": "24s2WItCbs16a261cca14",
    "status": "FAILED",
    "message": "transaction not found with id : '24s2WItCbs16a261cca14'"
}
```

#### 3. Get Account Balance API:
Description: Check wallet balance. Results are sorted by creation date. This doesn't include `CANCELLED` transactions.<br>
API: /wallet/{walletId}/transaction<br>
Method: GET<br>
Response:
```
HttpStatus = 200 (OK)
{
    "data": [
        {
            "type": "DEBIT",
            "amount": 1000.5,
            "summary": ""
        },
        {
            "type": "CREDIT",
            "amount": 10.5,
            "summary": ""
        },
        {
            "type": "CREDIT",
            "amount": 1000,
            "summary": ""
        }
    ],
    "status": "SUCCESS",
    "message": "Request processed successfully"
}
```
If wallet is Deactivated/Does Not Exist:
```
HttpStatus: 400 (BAD_REQUEST)
{
    "status": "FAILED",
    "message": "Failed to get passbook, Wallet is deactivated"
}
```

#### 4. Get wallet balance:
Description: Get wallet balance by wallet id. Returns `400 (BAD_REQUEST)` if wallet does not exists or wallet is deactivated.
API: /wallet/{walletId}
Method: GET<br>
Response:
```
HttpStatus = 200 (OK)
{
    "data": 10,
    "status": "SUCCESS"
}
```
If wallet is deactivated:
```
HttpStatus: 400 (BAD_REQUEST)
 {
    "status": "FAILED",
    "message": "wallet not found with id : '1'"
}
```

### Feature Enhancements:
1. Add basic auth
2. Create `transaction_history` table and use AOP to make entries into the table for any transaction modification.
