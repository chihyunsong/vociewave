// This is a manifest file that'll be compiled into application.js, which will include all the files
// listed below.
//
// Any JavaScript/Coffee file within this directory, lib/assets/javascripts, vendor/assets/javascripts,
// or vendor/assets/javascripts of plugins, if any, can be referenced here using a relative path.
//
// It's not advisable to add code directly here, but if you do, it'll appear at the bottom of the
// compiled file.
//
// Read Sprockets README (https://github.com/sstephenson/sprockets#sprockets-directives) for details
// about supported directives.
//
//= require jquery
//= require jquery_ujs
//= require bootstrap-sprockets
//= require turbolinks
//= require_tree .




function refresh_audio(audio_id, url){
  	var audio = $('#audio_id') 
  	audio.src = url;
  	audio.load();
  	//audio.play();        
}
function more_record(r, offset){
  
  $("#popular_record").append(offset.toString());
  

}
function increment_view(record_id){
	$.get("/increment_view", { id: record_id } );
}

function expand_comment_area(address, record_id){
   var div_id = '#comment_record_' + record_id;
   $.ajax({
     type: "GET",
     url: address, 
     success: function(data) {
          $(div_id).html(data);
     }
   });
}

function remove_comment_area(record_id){
  var div_id = '#comment_record_' + record_id;
  $(div_id).html('');
}

function show_share_address(address, title, description){
  title = encodeURIComponent(title);
  description = encodeURIComponent(description);
  $('#share_address').val(address);
  $("#twitter_share_button").attr('href', "https://twitter.com/share?url="+address+"&text="+title+'&hashtags=보이스웨이브,VoiceWave');
  $("#facebook_share_a_tag").attr('href', "https://www.facebook.com/dialog/feed?app_id=781345011984154&picture=https://voicewave.org/assets/voicewave.jpg&display=popup&caption="+title+"&name="+title+"&description="+description+"&link="+address+"&redirect_uri=https://voicewave.org");
  $('#share_modal').modal('show');
}

function popover(id){
  $("#"+id).popover({
    html : true,
    placement : 'top', 
    content: function() {
      return $('#'+id+'_content').html();
    },
    title: function() {
      return $('#'+id+'_title').html();
    }
  });
  $("#"+id).unbind('mouseleave');
}



function add_song_to_footer(title, nick_name, url, record_id, user_id){
  $('.individual_pause_button').hide();
  $('.individual_play_button').show();
  $('#play_' + record_id).hide();
  $('#pause_' + record_id).show();
  $('#play_global').hide();
  $('#pause_global').show();
  if (Amplitude.active.src.indexOf(url) > -1 ){
    // resume
    Amplitude.active.play();
  } else {
    NProgress.start();
    // play new song
    var new_song = {
        "name": title,
        "artist": title,
        "album": nick_name,
        "url": url,
        "record_id": record_id,
        "user_id": user_id
    }
    var ind = Amplitude.addSong(new_song);
    Amplitude.playNow(new_song, ind);
    $(Amplitude.active).attr('onloadeddata', "NProgress.done();");

    increment_view(record_id)
    $('#like_and_share').html('<a href="/increment_like?id='+record_id+'" type="button" class="btn btn-primary btn-sm" data-remote="true"><span class="glyphicon glyphicon-heart" aria-hidden="true"></span></a><button type="button" class="btn btn-success btn-sm" onclick="show_share_address(\'https://voicewave.org/records/'+record_id+'\')"><span class="glyphicon glyphicon-share" aria-hidden="true"></span></button><a href="/records/'+record_id+'" type="button" class="btn btn-default btn-sm show_progress_bar"><span class="glyphicon glyphicon-fullscreen" aria-hidden="true"></span></a>');
    $('#audio_footer').show();
    updatePlaylist();
  }
}

function pause_song(record_id){
  Amplitude.active.pause();
  $('#pause_' + record_id).hide();
  $('#play_' + record_id).show();
  $('#pause_global').hide();
  $('#play_global').show();
}

function play_global(){
  if (Amplitude.active.src == ""){
    alert('재생중인 보이스가 없습니다.');
  } else {
    Amplitude.active.play();
    $('#play_global').hide();
    $('#pause_global').show();
  }
}
function pause_global(){
  Amplitude.active.pause();
  $('#pause_global').hide();
  $('#play_global').show();
  $('.individual_pause_button').hide();
  $('.individual_play_button').show();
}

function next_global(){
  if (Amplitude.getActiveSongMetadata()['record_id']){
    NProgress.start();
    $('#play_global').hide();
    $('#pause_global').show();
    $('.individual_pause_button').hide();
    $('.individual_play_button').show();
    Amplitude.playNextOrPrev(Amplitude.getNextSong(), Amplitude.getNextSongIndex());
    $(Amplitude.active).attr('onloadeddata', "NProgress.done();");
    $('#play_' + Amplitude.getActiveSongMetadata()['record_id']).hide();
    $('#pause_' + Amplitude.getActiveSongMetadata()['record_id']).show();
    updatePlaylist();
  }
}

function prev_global(){
  if (Amplitude.getActiveSongMetadata()['record_id']){
    NProgress.start();
    $('#play_global').hide();
    $('#pause_global').show();
    $('.individual_pause_button').hide();
    $('.individual_play_button').show();
    Amplitude.playNextOrPrev(Amplitude.getPrevSong(), Amplitude.getPrevSongIndex());
    $(Amplitude.active).attr('onloadeddata', " NProgress.done();");
    $('#play_' + Amplitude.getActiveSongMetadata()['record_id']).hide();
    $('#pause_' + Amplitude.getActiveSongMetadata()['record_id']).show();
    updatePlaylist();
  }
}

function add_to_song_playlist(str){
  if (str == ""){
    alert('플레이리스트가 비어있습니다.');
  }else{
    var lists = str.split('!!!');
    
    var json_songs = {}
    json_songs['songs'] = [];
    json_songs["start_song"] = 1;
    var fist_index;
    for (var i=0; i<lists.length-1; i++){
      var data = lists[i].split('*!*');
      var tmp = {}
      tmp["name"] = data[1]
      tmp["artist"] = data[1]
      tmp["album"] = data[0]
      tmp["url"] = data[2]
      tmp["record_id"] = data[3]
      tmp["playlist_name"] = data[4]
      if (i == 0){
        first_index = Amplitude.addSong(tmp);
      }else{
        Amplitude.addSong(tmp);
      }
    }
    NProgress.start();
    $('#play_global').hide();
    $('#pause_global').show();
    Amplitude.playNow(Amplitude.getSongByIndex(first_index), first_index);
    $(Amplitude.active).attr('onloadeddata', "NProgress.done();");
    $('#play_' + Amplitude.getActiveSongMetadata()['record_id']).hide();
    $('#pause_' + Amplitude.getActiveSongMetadata()['record_id']).show();
    updatePlaylist();
  } 
}

function playSong(index){
  NProgress.start();
  $('#play_global').hide();
  $('#pause_global').show()
  Amplitude.playNow(Amplitude.getSongByIndex(index), index);
  $(Amplitude.active).attr('onloadeddata', "NProgress.done();");
  $('#play_' + Amplitude.getActiveSongMetadata()['record_id']).hide();
  $('#pause_' + Amplitude.getActiveSongMetadata()['record_id']).show();
  updatePlaylist();
}

function updatePlaylist(){
  $('#like_and_share').html('<div class="dropup global_playlist_button" style="float:left"><a data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" type="button" class="btn btn-info btn-sm"><span class="glyphicon glyphicon-music" aria-hidden="true"></span></a><ul class="dropdown-menu" id="global_playlist"></ul></div><a href="/increment_like?id='+Amplitude.getActiveSongMetadata()['record_id']+'" type="button" class="btn btn-primary btn-sm" data-remote="true"><span class="glyphicon glyphicon-heart" aria-hidden="true"></span></a><button type="button" class="btn btn-success btn-sm" onclick="show_share_address(\'https://voicewave.org/records/'+Amplitude.getActiveSongMetadata()['record_id']+'\', \''+Amplitude.getActiveSongMetadata()['name']+'\',\'' +Amplitude.getActiveSongMetadata()['name'] +'\')"><span class="glyphicon glyphicon-share" aria-hidden="true"></span></button><a href="/records/'+Amplitude.getActiveSongMetadata()['record_id']+'" type="button" class="btn btn-default btn-sm show_progress_bar"><span class="glyphicon glyphicon-fullscreen" aria-hidden="true"></span></a>');    

  //progress bar
  configureNProgress();

  var playlist_info = Amplitude.playlistInfo();
  $('#global_playlist').html('');
  for (var i=0; i < playlist_info.length; i++){
    if (parseInt(Amplitude.getCurrentIndex()) == i) { 
      $('#global_playlist').append('<li class="active"><a onclick="playSong('+i.toString()+');">'+playlist_info[i]["name"]+'</a></li>');
    } else {
      $('#global_playlist').append('<li><a onclick="playSong('+i.toString()+');">'+(i+1).toString()+'. '+playlist_info[i]["name"]+'</a></li>');  
    }
  }
  var title = Amplitude.getActiveSongMetadata()['name'];
  var user = Amplitude.getActiveSongMetadata()['album'];
  if (title.length > 10){
    title = title.substring(0, 8) + '...';
  }
  if (user.length > 15){
    title = title.substring(0, 12) + '...';
  }
  $('#title_and_user').html('<span id="now-playing-artist" amplitude-song-info="artist">' + title + '</span><br><span id="now-playing-album" amplitude-song-info="album" style="float:left">' + user + '</span>');
  Amplitude.setVolume();
}

function configureNProgress(){
  NProgress.configure({ trickleRate: 0.15, trickleSpeed: 100 });
  $(".show_progress_bar").click(function(){
    NProgress.start();
  });
}
