class RenameColumnReportTable < ActiveRecord::Migration
  def change
  	rename_column :reports, :report_id, :record_id
  end
end
