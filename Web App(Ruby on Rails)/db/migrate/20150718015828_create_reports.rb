class CreateReports < ActiveRecord::Migration
  def change
    create_table :reports do |t|
      t.integer :user_id
      t.integer :report_id
      t.integer :category
      t.string :content
      t.boolean :chk_admin

      t.timestamps null: false
    end
  end
end
