class AddFilePathToRecord < ActiveRecord::Migration
  def change
  	add_column :records, :file_path, :string
  end
end
