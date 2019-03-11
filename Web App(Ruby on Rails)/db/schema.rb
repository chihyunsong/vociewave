# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20150723161860) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "categories", force: :cascade do |t|
    t.string   "name"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "comments", force: :cascade do |t|
    t.text     "description"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "user_id"
    t.integer  "record_id"
  end

  add_index "comments", ["record_id"], name: "index_comments_on_record_id", using: :btree
  add_index "comments", ["user_id"], name: "index_comments_on_user_id", using: :btree

  create_table "followers", force: :cascade do |t|
    t.integer  "follower_id"
    t.integer  "followed_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "followers", ["followed_id", "follower_id"], name: "index_followers_on_followed_id_and_follower_id", unique: true, using: :btree
  add_index "followers", ["followed_id"], name: "index_followers_on_followed_id", using: :btree
  add_index "followers", ["follower_id"], name: "index_followers_on_follower_id", using: :btree

  create_table "notifications", force: :cascade do |t|
    t.integer  "category"
    t.integer  "user_id"
    t.integer  "who_id"
    t.integer  "what_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "notifications", ["user_id"], name: "index_notifications_on_user_id", using: :btree

  create_table "pg_search_documents", force: :cascade do |t|
    t.text     "content"
    t.integer  "searchable_id"
    t.string   "searchable_type"
    t.datetime "created_at",      null: false
    t.datetime "updated_at",      null: false
  end

  add_index "pg_search_documents", ["searchable_type", "searchable_id"], name: "index_pg_search_documents_on_searchable_type_and_searchable_id", using: :btree

  create_table "playlists", force: :cascade do |t|
    t.string   "title"
    t.integer  "user_id"
    t.integer  "record_ids",       default: [],                 array: true
    t.datetime "created_at",                       null: false
    t.datetime "updated_at",                       null: false
    t.boolean  "private",          default: false
    t.string   "pic_file_name"
    t.string   "pic_content_type"
    t.integer  "pic_file_size"
    t.datetime "pic_updated_at"
    t.integer  "view",             default: 0,     null: false
    t.integer  "like",             default: 0,     null: false
  end

  create_table "records", force: :cascade do |t|
    t.integer  "user_id"
    t.integer  "private",           default: 0
    t.integer  "like",              default: 0
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "view",              default: 0,   null: false
    t.string   "title",             default: "0", null: false
    t.string   "description"
    t.integer  "report",            default: 0,   null: false
    t.integer  "category_id"
    t.string   "file_file_name"
    t.string   "file_content_type"
    t.integer  "file_file_size"
    t.datetime "file_updated_at"
  end

  create_table "reports", force: :cascade do |t|
    t.integer  "user_id"
    t.integer  "record_id"
    t.integer  "category"
    t.string   "content"
    t.boolean  "chk_admin"
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
  end

  create_table "table_user_likes_playlists", force: :cascade do |t|
    t.integer "user_id"
    t.integer "playlist_id"
  end

  add_index "table_user_likes_playlists", ["playlist_id"], name: "index_table_user_likes_playlists_on_playlist_id", using: :btree
  add_index "table_user_likes_playlists", ["user_id"], name: "index_table_user_likes_playlists_on_user_id", using: :btree

  create_table "table_user_likes_records", force: :cascade do |t|
    t.integer "user_id"
    t.integer "record_id"
  end

  add_index "table_user_likes_records", ["record_id"], name: "index_table_user_likes_records_on_record_id", using: :btree
  add_index "table_user_likes_records", ["user_id"], name: "index_table_user_likes_records_on_user_id", using: :btree

  create_table "taggings", force: :cascade do |t|
    t.integer  "tag_id"
    t.integer  "taggable_id"
    t.string   "taggable_type"
    t.integer  "tagger_id"
    t.string   "tagger_type"
    t.string   "context",       limit: 128
    t.datetime "created_at"
  end

  add_index "taggings", ["tag_id", "taggable_id", "taggable_type", "context", "tagger_id", "tagger_type"], name: "taggings_idx", unique: true, using: :btree
  add_index "taggings", ["taggable_id", "taggable_type", "context"], name: "index_taggings_on_taggable_id_and_taggable_type_and_context", using: :btree

  create_table "tags", force: :cascade do |t|
    t.string  "name"
    t.integer "taggings_count", default: 0
  end

  add_index "tags", ["name"], name: "index_tags_on_name", unique: true, using: :btree

  create_table "users", force: :cascade do |t|
    t.string   "password"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "email",                    default: "",    null: false
    t.string   "encrypted_password",       default: "",    null: false
    t.string   "reset_password_token"
    t.datetime "reset_password_sent_at"
    t.datetime "remember_created_at"
    t.integer  "sign_in_count",            default: 0,     null: false
    t.datetime "current_sign_in_at"
    t.datetime "last_sign_in_at"
    t.string   "current_sign_in_ip"
    t.string   "last_sign_in_ip"
    t.integer  "fame",                     default: 0,     null: false
    t.string   "nick_name"
    t.string   "confirmation_token"
    t.datetime "confirmed_at"
    t.datetime "confirmation_sent_at"
    t.string   "unconfirmed_email"
    t.string   "provider"
    t.string   "uid"
    t.string   "api_key"
    t.string   "description"
    t.string   "profile_pic_file_name"
    t.string   "profile_pic_content_type"
    t.integer  "profile_pic_file_size"
    t.datetime "profile_pic_updated_at"
    t.boolean  "admin",                    default: false
  end

  add_index "users", ["confirmation_token"], name: "index_users_on_confirmation_token", unique: true, using: :btree
  add_index "users", ["email"], name: "index_users_on_email", unique: true, using: :btree
  add_index "users", ["reset_password_token"], name: "index_users_on_reset_password_token", unique: true, using: :btree

end
