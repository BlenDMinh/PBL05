/**
 * @param { import("knex").Knex } knex
 * @returns { Promise<void> }
 */
const { fakerVI } = require("@faker-js/faker");

const NUM_USERS = 100;
exports.NUM_USERS = NUM_USERS;

const {
  USER_ROLE,
  USER_STATUS,
} = require("../migrations/20240218091049_create_users");

const tableName = "users";
exports.seed = async function (knex) {
  // Deletes ALL existing entries
  await knex(tableName).del();
  const users = Array.from({ length: NUM_USERS - 2 }, (_, index) => ({
    display_name: fakerVI.person.fullName(),
    email: fakerVI.internet.email(),
    avatar_url: fakerVI.image.avatar(),
    status: USER_STATUS.ACTIVE,
    role: USER_ROLE.USER_ROLE,
  }));
  await knex(tableName).insert(users);
  await knex(tableName).insert([
    {
      display_name: "admin1",
      email: "admin1@gmail.com",
      role: USER_ROLE.ADMIN,
      status: USER_STATUS.ACTIVE,
    },
    {
      display_name: "player1",
      email: "player1@gmail.com",
      role: USER_ROLE.PLAYER,
      status: USER_STATUS.ACTIVE,
    },
    {
      display_name: "player2",
      email: "player2@gmail.com",
      role: USER_ROLE.PLAYER,
      status: USER_STATUS.ACTIVE,
    },
  ]);
};
