class FixReferenceRecordCategory < ActiveRecord::Migration
  def change
  	#remove_reference(:records, :categories, index: true, foreign_key: true)
  	add_column :records, :category_id, :integer, index: true
  end
end
