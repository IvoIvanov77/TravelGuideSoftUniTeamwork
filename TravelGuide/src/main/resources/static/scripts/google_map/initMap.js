let csrfHeader = $("meta[name='_csrf_header']").attr("content");
let csrfToken = $("meta[name='_csrf']").attr("content");

let map;
let marker;
let currentMarker;
let infowindow;
let messagewindow;

$(document).ready(function () {

    function initAutocomplete() {
        let map = new google.maps.Map(document.getElementById('map'), {
            center: {lat: -33.8688, lng: 151.2195},
            zoom: 13,
            mapTypeId: 'roadmap'
        });

        //Create the info/input window that will show on a marker click
        let markForm = document.getElementById('mapForm');
        infowindow = new google.maps.InfoWindow({
            content: markForm
        });
        $(markForm).hide();

        // A message window that will return information about the process
        messagewindow = new google.maps.InfoWindow({
            content: document.getElementById('message')
        });

        //Create marker on the map and open the info window on click
        google.maps.event.addListener(map, 'click', function (event) {
            marker = new google.maps.Marker({
                position: event.latLng,
                map: map
            });

            google.maps.event.addListener(marker, 'click', function () {
                $(markForm).show();
                infowindow.open(map, marker);
                currentMarker = this;
            });

            google.maps.event.addListener(messagewindow, 'closeclick', function () {
                currentMarker.setMap(null);
            })
        });

        // Create the search box and link it to the UI element.
        let input = document.getElementById('pac-input');
        let searchBox = new google.maps.places.SearchBox(input);
        map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

        // Bias the SearchBox results towards current map's viewport.
        map.addListener('bounds_changed', function () {
            searchBox.setBounds(map.getBounds());
        });

        let markers = [];
        // Listen for the event fired when the user selects a prediction and retrieve
        // more details for that place.
        searchBox.addListener('places_changed', function () {
            let places = searchBox.getPlaces();

            if (places.length === 0) {
                return;
            }

            // Clear out the old markers.
            markers.forEach(function (marker) {
                marker.setMap(null);
            });
            markers = [];

            // For each place, get the icon, name and location.
            let bounds = new google.maps.LatLngBounds();
            places.forEach(function (place) {
                if (!place.geometry) {
                    console.log("Returned place contains no geometry");
                    return;
                }
                let icon = {
                    url: place.icon,
                    size: new google.maps.Size(71, 71),
                    origin: new google.maps.Point(0, 0),
                    anchor: new google.maps.Point(17, 34),
                    scaledSize: new google.maps.Size(25, 25)
                };

                // Create a marker for each place.
                markers.push(new google.maps.Marker({
                    map: map,
                    icon: icon,
                    title: place.name,
                    position: place.geometry.location
                }));

                if (place.geometry.viewport) {
                    // Only geocodes have viewport.
                    bounds.union(place.geometry.viewport);
                } else {
                    bounds.extend(place.geometry.location);
                }
            });
            map.fitBounds(bounds);
        });

        // // Place the approved marker on the map
        // function placeMarkerAndPanTo(latLng, map) {
        //     let marker = new google.maps.Marker({
        //         position: latLng,
        //         map: map
        //     });
        //     map.panTo(latLng);
        // }

        // ////Marker clusterer functionality
        // // // Create an array of alphabetical characters used to label the markers.
        // // let labels = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
        // //
        // // // Add some markers to the map.
        // // // Note: The code uses the JavaScript Array.prototype.map() method to
        // // // create an array of markers based on a given "locations" array.
        // // // The map() method here has nothing to do with the Google Maps API.
        // // let markers = locations.map(function(location, i) {
        // //     return new google.maps.Marker({
        // //         position: location,
        // //         label: labels[i % labels.length]
        // //     });
        // // });
        // //
        // // // Add a marker clusterer to manage the markers.
        // // let markerCluster = new MarkerClusterer(map, markers,
        // //     {imagePath: '/static/images/google_map/m1.png'});
    }

    initAutocomplete();
});
    //Send the data retrieved from the user to the DB
    function saveData() {
        let event = document.getElementById('event').value;
        let comments = document.getElementById('comment').value;
        let latlng = marker.getPosition();
        let destId = $('#infoSubmit').attr("data-dest_id");

        // let data = new FormData();
        // jQuery.each(jQuery('#image')[0].files, function(i, file) {
        //     data.append('file-'+i, file);
        // });
        let data = {};
        data['event'] = event;
        data["comments"] = comments;
        data["lat"] = latlng.lat();
        data["lon"] = latlng.lng();
        data["dest_id"] = destId;

        sendDataToDb(data)
    }

//Ajax request - submit user data to controller and return the result
    function sendDataToDb(request) {
        let headers = {};
        headers[csrfHeader] = csrfToken;

        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/mark/request",
            headers: headers,
            data: JSON.stringify(request),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function (data) {
                infowindow.close();
                $('#message').append("<p></p>").text(data.message);
                messagewindow.open(map, marker);
            },
            error: function (e) {
                let message = "Something went wrong: "
                    + e.responseText;
                $('#message').text(message);
                infowindow.close();
                messagewindow.open(map, marker);
            }
        });
    }
// let locations = [
//     {lat: -31.563910, lng: 147.154312},
//     {lat: -33.718234, lng: 150.363181},
//     {lat: -33.727111, lng: 150.371124},
//     {lat: -33.848588, lng: 151.209834},
//     {lat: -33.851702, lng: 151.216968},
//     {lat: -34.671264, lng: 150.863657},
//     {lat: -35.304724, lng: 148.662905},
//     {lat: -36.817685, lng: 175.699196},
//     {lat: -36.828611, lng: 175.790222},
//     {lat: -37.750000, lng: 145.116667},
//     {lat: -37.759859, lng: 145.128708},
//     {lat: -37.765015, lng: 145.133858},
//     {lat: -37.770104, lng: 145.143299},
//     {lat: -37.773700, lng: 145.145187},
//     {lat: -37.774785, lng: 145.137978},
//     {lat: -37.819616, lng: 144.968119},
//     {lat: -38.330766, lng: 144.695692},
//     {lat: -39.927193, lng: 175.053218},
//     {lat: -41.330162, lng: 174.865694},
//     {lat: -42.734358, lng: 147.439506},
//     {lat: -42.734358, lng: 147.501315},
//     {lat: -42.735258, lng: 147.438000},
//     {lat: -43.999792, lng: 170.463352}
// ]



