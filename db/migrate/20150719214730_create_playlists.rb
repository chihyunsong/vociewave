class CreatePlaylists < ActiveRecord::Migration
  def change
    create_table :playlists do |t|
      t.string :title
      t.text :description
      t.integer :user_id
      t.integer :record_ids, array: true, default: [] 	
      t.timestamps null: false
    end
  end
end
