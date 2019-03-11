class Fixupforeignkey < ActiveRecord::Migration
  def change
  	remove_foreign_key :comments, :users
    remove_foreign_key :comments, :records
  end
end
