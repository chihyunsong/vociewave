class CreateFollowers < ActiveRecord::Migration
  def change
    create_table :followers do |t|
    	t.integer :follower_id, index: true
    	t.integer :followed_id, index: true
    	t.timestamps
    end
  end
end
