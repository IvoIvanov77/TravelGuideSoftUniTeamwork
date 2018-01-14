let csrfHeader = $("meta[name='_csrf_header']").attr("content");
let csrfToken = $("meta[name='_csrf']").attr("content");

let geocoder;
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

        geocoder = new google.maps.Geocoder();

        let location = $("#destination").attr("data-dest_name");

        geocoder.geocode({'address': location}, function (results, status) {
            if (status === google.maps.GeocoderStatus.OK) {

                // reposition map to the first returned location
                map.setCenter(results[0].geometry.location);

                // put marker on map
                let marker = new google.maps.Marker({
                    map: map,
                    position: results[0].geometry.location
                });
            }
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




