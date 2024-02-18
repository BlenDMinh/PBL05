# Server handler
Move to backend folder
```bash
$ cd backend
```
### To start server

```bash
$ docker compose up -d --build
```


Access `localhost:8080/chess-backend` to view server status



### To restart server


```bash
# Down container 
$ docker compose down api
$ docker compose up -d --build api
```


# Database Hanlder
Ensure that db container is running !!!\
Move to database folder
```bash
$ cd database
```

```bash
# Install dependencies
$ npm install

# Reset database
$ npm run db:reset

# Create new migration file
$ npm run knex migrate:make <migration_name_file.js>

# Run the migration
$ npm run knex migrate:latest

# Rollback the migration
$ npm run knex migrate:rollback
```

### Seeding

```bash
# Create seed file
$ npm run knex seed:make <seed_name_file.js>

# Run all seed file
$ npm run knex seed:run

# Run specific seed file
$ npm run knex seed:run --specific=<seed-filename.js>
```

For more information you can read at Knexjs docs

https://knexjs.org/guide/migrations.html#migration-cli

