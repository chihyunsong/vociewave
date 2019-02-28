class CreateCategories < ActiveRecord::Migration
  def change
    create_table :categories do |t|
    	t.string :name
     	t.timestamps
    end
    #add_reference :records, :categories, index: true, foreign_key: true
  end
end
