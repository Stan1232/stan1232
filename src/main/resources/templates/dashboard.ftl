<#import "/spring.ftl" as spring />
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0"/>
    <title>StarGUI Web</title>

    <!-- CSS  -->
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link href='http://fonts.googleapis.com/css?family=Roboto:400,300,500' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/css/materialize.min.css">
    <link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.3/leaflet.css" />
    <link rel="stylesheet" href="http://www.liedman.net/leaflet-routing-machine/dist/leaflet-routing-machine.css" />
    <link href='https://api.mapbox.com/mapbox.js/plugins/leaflet-label/v0.2.1/leaflet.label.css' rel='stylesheet' />

    <link href="<@spring.url "/static/css/global.css" />" rel="stylesheet">

</head>
<body>

<#include "navbar.ftl" />

<!--  Scripts-->
<script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/js/materialize.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-validate/1.14.0/jquery.validate.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.0.3/sockjs.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.3/leaflet.js"></script>
<script src="http://www.liedman.net/leaflet-routing-machine/dist/leaflet-routing-machine.js"></script>
<script src='https://api.mapbox.com/mapbox.js/plugins/leaflet-label/v0.2.1/leaflet.label.js'></script>


<script type="text/javascript">
    window.stargui = window.stargui || {};
    window.stargui.BASE_URL = "<@spring.url "" />";
    $("#contents").show();
</script>

<script src="<@spring.url "/static/js/loginEvents.js" />"></script>
<#--<script src="<@spring.url "/static/js/mqmessages.js" />"></script>
<script src="<@spring.url "/static/js/map.js" />"></script>
<script src="<@spring.url "/static/js/time.js" />"></script>-->

</body>
</html>