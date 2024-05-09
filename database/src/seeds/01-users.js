/**
 * @param { import("knex").Knex } knex
 * @returns { Promise<void> }
 */
const { fakerVI } = require("@faker-js/faker");

const NUM_USERS = 100;
exports.NUM_USERS = NUM_USERS;
exports.NUM_RAN_USERS = NUM_USERS - 3;
const {
  USER_ROLE,
  USER_STATUS,
} = require("../migrations/20240218091049_create_users");

const tableName = "users";
exports.seed = async function (knex) {
  // Deletes ALL existing entries
  await knex(tableName).del();
  const users = Array.from({ length: NUM_USERS - 3 }, (_, index) => ({
    display_name: fakerVI.person.fullName(),
    email: fakerVI.internet.email(),
    avatar_url: fakerVI.image.avatar(),
    status: USER_STATUS.ACTIVE,
    role: USER_ROLE.USER_ROLE,
    elo: Math.floor(Math.random() * 1000) + 1000,
    created_at: fakerVI.date.past(),
  }));
  await knex(tableName).insert(users);
  await knex(tableName).insert([
    {
      display_name: "admin1",
      email: "admin1@gmail.com",
      role: USER_ROLE.ADMIN,
      status: USER_STATUS.ACTIVE,
      avatar_url: "https://avatars.githubusercontent.com/u/56352885",
      created_at: fakerVI.date.past(),
    },
    {
      display_name: "player1",
      email: "player1@gmail.com",
      role: USER_ROLE.PLAYER,
      status: USER_STATUS.ACTIVE,
      created_at: fakerVI.date.past(),
      avatar_url:
        "https://cloudflare-ipfs.com/ipfs/Qmd3W5DuhgHirLHGVixi6V76LhCkZUz6pnFt5AJBiyvHye/avatar/472.jpg",
    },
    {
      display_name: "player2",
      email: "player2@gmail.com",
      role: USER_ROLE.PLAYER,
      status: USER_STATUS.ACTIVE,
      created_at: fakerVI.date.past(),
      avatar_url:
        "https://cloudflare-ipfs.com/ipfs/Qmd3W5DuhgHirLHGVixi6V76LhCkZUz6pnFt5AJBiyvHye/avatar/205.jpg",
    },
  ]);
};
