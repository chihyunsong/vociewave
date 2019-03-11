class AddModels < ActiveRecord::Migration
  def change
  	add_column :users, :fame, :integer, null: false, :default => 0
  	add_column :records, :view, :integer, null: false, :default => 0
  	add_column :records, :title, :string, null: false, :default => 0
  	add_column :records, :description, :string
  	add_column :records, :report, :integer, null: false, :default => 0
  end
end
