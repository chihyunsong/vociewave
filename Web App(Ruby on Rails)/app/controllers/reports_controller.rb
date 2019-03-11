class ReportsController < ApplicationController
  before_action :set_report, only: [:update, :destroy, :ignore]
  before_action :authenticate_user!
  before_action :user_admin!, only: [:index, :ignore, :destroy, :update]
  respond_to :html,:js
  # GET /reports
  # GET /reports.json
  def index
    @reports = Report.all.where(chk_admin:false)
  end

  def get_form
    @record = Record.find(params[:id])

    if @record.nil?
      @error ="no way!"
    else
      if @record.user_id === current_user.id
        @error = "no1"
      else
        @report = Report.where(user_id: current_user.id, record_id: @record.id).first
        if !@report.nil?
          @error = "no2"
        end
      end
    end
  end

  # GET /reports/new
  def new
    @report = Report.new
  end


  # GET /reports/1/ignore
  def ignore

    @report.update(chk_admin:true)
    respond_with(@report)
  end

  def report
    
    @record = Record.find(params[:record_id])
    if @record.nil?
      @error = "Record does not exist."
    else
      @report = Report.new({:user_id => current_user.id, :record_id => params[:record_id], :content => params[:content],:category => params[:category].to_i,:chk_admin=>false, :created_at => Time.now})
      if @report.save
      else
        @error = 'Your report is too short'
      
      end
    end
    respond_with(@report)
  end

  # PATCH/PUT /reports/1
  # PATCH/PUT /reports/1.json
  def update
    respond_to do |format|
      if @report.update(report_params)
        format.html { redirect_to @report, notice: 'Report was successfully updated.' }
        format.json { render :show, status: :ok, location: @report }
      else
        format.html { render :edit }
        format.json { render json: @report.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /reports/1
  # DELETE /reports/1.json
  def destroy
    @report.destroy
    respond_to do |format|
      format.html { redirect_to reports_url, notice: 'Report was successfully destroyed.' }
      format.json { head :no_content }
    end
  end

  private
    def user_admin!
      unless !current_user.admin.nil? && current_user.admin == true
        render 'my_js'
      end
    end

    # Use callbacks to share common setup or constraints between actions.
    def set_report
      @report = Report.find(params[:id])
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def report_params
      params.require(:report).permit(:user_id, :record_id, :category, :content, :chk_admin)
    end
end
