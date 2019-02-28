json.array!(@reports) do |report|
  json.extract! report, :id, :user_id, :report_id, :category, :content, :chk_admin
  json.url report_url(report, format: :json)
end
