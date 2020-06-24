
Oneclick is a Terraform UI for your Terraform modules, and self-service infrastructure.

### What is it?

Oneclick is a web application to import and run your Terraform modules.
It features : 
* importing modules from source code (Github/Gitlab)
* validation of Terraform variables values (mandatory variables, regex-based validation)
* setting up default values or masking variables for your users
* running modules (plan/apply/destroy) in one click and managing Terraform state
* team management

### Requirements

Oneclick needs :
 * a docker daemon (used to run Terraform itself)
 * and a MongoDb database (to store its data)
   * we currently support MongoDb 4.0 only

### Quick start

Start Oneclick with `docker-compose` quickly !

Clone this repository, and just run `docker-compose up -d`. 

The docker-compose will start a Oneclick server, listening on http://localhost:8080, and a MongoDb database.

Default credentials for entering:

- Admin account `ROLE_ADMIN`:

  ```
  Username: admin
  Password: admin123
  ```

- User account `ROLE_USER`:

  ```
  Username: user
  Password: user123
  ```
  