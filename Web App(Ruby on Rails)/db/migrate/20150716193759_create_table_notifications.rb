class CreateTableNotifications < ActiveRecord::Migration
  def change
    create_table :notifications do |t|
    	t.integer :category
    	t.integer :user_id
    	t.integer :who_id
    	t.integer :what_id
    	t.timestamps
    	t.index :user_id
    end
  end
end
