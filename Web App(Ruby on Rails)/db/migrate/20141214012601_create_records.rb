class CreateRecords < ActiveRecord::Migration
  def change
    create_table :records do |t|
      t.integer :user_id
      t.integer :private, :default => 0
      t.integer :like, :default => 0
      t.timestamps
    end
  end
end
