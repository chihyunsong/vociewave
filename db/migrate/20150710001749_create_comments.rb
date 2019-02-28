class CreateComments < ActiveRecord::Migration
  def change
    create_table :comments do |t|
    	t.text :description
    	t.timestamps
    end
    add_reference :comments, :user, index: true, foreign_key: true
    add_reference :comments, :record, index: true, foreign_key: true
  end
end
