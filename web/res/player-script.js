/**
 * Created by Jeremias on 06.08.2017.
 */
var myPlayer = videojs('my-player');
myPlayer.ready(function() {
  myPlayer.volume(0.1);
  myPlayer.play();
});