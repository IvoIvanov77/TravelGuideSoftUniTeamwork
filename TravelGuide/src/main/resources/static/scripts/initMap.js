function initMap() {
    var uluru = {lat: 42.679361, lng: 23.36681199999998};
    var map = new google.maps.Map(document.getElementById('map'), {
        zoom: 4,
        center: uluru
    });
    var marker = new google.maps.Marker({
        position: uluru,
        map: map
    });
}