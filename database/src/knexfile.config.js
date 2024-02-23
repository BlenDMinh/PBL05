const dotenv = require('dotenv')
const { join } = require('path')

dotenv.config({ path: join(__dirname, '../../backend/.env') })

module.exports = {
  development: {
    client: 'pg',
    connection: {
      host: 'localhost',
      port: process.env.DB_PORT,
      user: process.env.POSTGRES_USER,
      password: process.env.POSTGRES_PASSWORD,
      database: process.env.POSTGRES_DB,
      charset: 'utf8',
    },
    migrations: {
      directory: `${__dirname}/migrations`,
    },
    seeds: {
      directory: `${__dirname}/seeds`,
    },
  },
}
