class AddPaperclipToRecords < ActiveRecord::Migration
  def change
  	remove_column :records, :file_path
  	add_attachment :records, :file
  end
end
