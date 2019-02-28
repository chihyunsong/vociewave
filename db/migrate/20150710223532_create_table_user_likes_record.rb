class CreateTableUserLikesRecord < ActiveRecord::Migration
  def change
    create_table :table_user_likes_records do |t|
    	t.integer :user_id
    	t.integer :record_id
    	t.index :user_id
    	t.index :record_id
    end
  end
end
