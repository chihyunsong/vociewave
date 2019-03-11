class AddViewLike < ActiveRecord::Migration
  def change

  	add_column :playlists, :view, :integer, null: false, :default => 0
  	create_table :table_user_likes_playlists do |t|
    	t.integer :user_id
    	t.integer :playlist_id
    	t.index :user_id
    	t.index :playlist_id
    end
  
  end
end
