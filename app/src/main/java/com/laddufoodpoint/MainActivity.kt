package com.laddufoodpoint

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.location.Location
import android.os.Build
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import java.util.Locale
import java.net.HttpURLConnection
import java.net.URL
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.RiceBowl
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.core.view.WindowCompat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private var appDarkTheme = false
private val Ink get() = if(appDarkTheme) Color(0xFFF4F4F4) else Color(0xFF202124)
private val Gold=Color(0xFFFC8019)
private val Cream get() = if(appDarkTheme) Color(0xFF101114) else Color(0xFFFFFFFF)
private val Muted get() = if(appDarkTheme) Color(0xFFB0B3BA) else Color(0xFF777B82)
private val CardColor get() = if(appDarkTheme) Color(0xFF1B1D22) else Color.White
private data class Food(val name:String,val category:String,val single:Int,val plate:Int?=null)
private data class Line(val food:Food,val variant:String,val price:Int,val qty:Int)
private data class SavedAddress(val label:String,val details:String,val landmark:String="",val addressType:String="Other",val latitude:Double?=null,val longitude:Double?=null)
private data class OrderRecord(val id:String,val total:Int,val description:String,val customerName:String,val phone:String,val address:String,val fulfillment:String,val paymentMethod:String,val itemsJson:String,val itemTotal:Int,val deliveryFee:Int,val packagingFee:Int,val distanceKm:Double,val preferences:String,val status:String,val createdAt:Long)
private const val RESTAURANT_MAP_URL = "https://maps.app.goo.gl/hc9ea46RRy5WBW9t6?g_st=ac"
private const val RESTAURANT_ADDRESS = "12-11-692, Warasiguda X Road, Secunderabad, Padmarao Nagar, Hyderabad"
private val menu=listOf(
 Food("Egg Fried Rice","NON-VEG",70,130),Food("Egg Bhurji","NON-VEG",60,120),
 Food("Chicken Fried Rice","NON-VEG",100,170),Food("Egg Noodles","NON-VEG",70,130),
 Food("Chicken 65","NON-VEG",120,220),Food("Chicken Manchurian","NON-VEG",120,220),
 Food("Chicken Drumstick","NON-VEG",120,240),Food("Chicken Chilli","NON-VEG",130,230),
 Food("Schezwan Chicken","NON-VEG",70,120),Food("Chicken Leg Piece (1 pc)","NON-VEG",100),
 Food("Veg Fried Rice","VEG",60,110),Food("Veg Noodles","VEG",60,110),
 Food("Veg Manchurian","VEG",80,140),Food("Veg Manchurian Noodles","VEG",80,140),
 Food("Jeera Fried Rice","VEG",60,110),Food("Rumali Roti","ROTI",12),
 Food("Tandoori Roti","ROTI",13),Food("Butter Naan","ROTI",30)
)
private fun itemKey(f:Food,v:String)=f.name+"|"+v
private fun foodPhoto(f:Food):String {
 val id = when(f.name) {
  "Egg Fried Rice" -> "photo-1603133872878-684f208fb84b"
  "Egg Bhurji" -> "photo-1525351484163-7529414344d8"
  "Chicken Fried Rice" -> "photo-1603133872878-684f208fb84b"
  "Egg Noodles" -> "photo-1569718212165-3a8278d5f624"
  "Chicken 65" -> "photo-1603894584373-5ac82b2ae398"
  "Chicken Manchurian" -> "photo-1604908176997-125f25cc6f3d"
  "Chicken Drumstick" -> "photo-1626082927389-6cd097cdc6ec"
  "Chicken Chilli" -> "photo-1603360946369-dc9bb6258143"
  "Schezwan Chicken" -> "photo-1603894584373-5ac82b2ae398"
  "Chicken Leg Piece (1 pc)" -> "photo-1626082927389-6cd097cdc6ec"
  "Veg Fried Rice" -> "photo-1512058564366-18510be2db19"
  "Veg Noodles" -> "photo-1569718212165-3a8278d5f624"
  "Veg Manchurian" -> "photo-1601050690597-df0568f70950"
  "Veg Manchurian Noodles" -> "photo-1569718212165-3a8278d5f624"
  "Jeera Fried Rice" -> "photo-1512058564366-18510be2db19"
  "Rumali Roti" -> "photo-1601050690597-df0568f70950"
  "Tandoori Roti" -> "photo-1601050690597-df0568f70950"
  "Butter Naan" -> "photo-1626082927389-6cd097cdc6ec"
  else -> "photo-1565299624946-b28f40a0ae38"
 }
 return "https://images.unsplash.com/" + id + "?w=900&auto=format&fit=crop&q=85"
}

class MainActivity:ComponentActivity(){
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState); WindowCompat.setDecorFitsSystemWindows(window,true); window.statusBarColor=android.graphics.Color.WHITE; window.navigationBarColor=android.graphics.Color.WHITE; WindowCompat.getInsetsController(window,window.decorView).isAppearanceLightStatusBars=true; WindowCompat.getInsetsController(window,window.decorView).isAppearanceLightNavigationBars=true; setContent{LBFPApp()}}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun LBFPApp(){
 val context = LocalContext.current
 val prefs = remember { context.getSharedPreferences("lbfp_customer_data", android.content.Context.MODE_PRIVATE) }
 var themeMode by remember { mutableStateOf(prefs.getString("theme_mode","system") ?: "system") }
 val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
 val darkMode = themeMode=="dark" || (themeMode=="system" && systemDark)
 appDarkTheme=darkMode
 SideEffect { val activity=context as ComponentActivity; val bars=WindowCompat.getInsetsController(activity.window,activity.window.decorView); bars.isAppearanceLightStatusBars=!darkMode; bars.isAppearanceLightNavigationBars=!darkMode; activity.window.statusBarColor=if(darkMode) android.graphics.Color.rgb(16,17,20) else android.graphics.Color.WHITE; activity.window.navigationBarColor=if(darkMode) android.graphics.Color.rgb(16,17,20) else android.graphics.Color.WHITE }
 var tab by remember{mutableStateOf("Home")}
 var orderFilter by remember{mutableStateOf("All")}
 var selectedFood by remember{mutableStateOf<Food?>(null)}
 var category by remember{mutableStateOf("ALL")}
 var cart by remember{mutableStateOf(runCatching { val a=JSONArray(prefs.getString("cart","[]")); buildMap<String,Line> { for(i in 0 until a.length()){ val o=a.getJSONObject(i); val f=menu.firstOrNull { it.name==o.optString("name") } ?: continue; val v=o.optString("variant"); val p=o.optInt("price"); val q=o.optInt("qty"); if(q>0 && p>0) put(itemKey(f,v),Line(f,v,p,q)) } } }.getOrDefault(emptyMap()))}
 var cartOpen by remember{mutableStateOf(false)}
 var checkout by remember{mutableStateOf(false)}
 var name by remember{mutableStateOf(prefs.getString("customer_name","") ?: "")}
 var phone by remember{mutableStateOf(prefs.getString("customer_phone","") ?: "")}
 var address by remember{mutableStateOf(prefs.getString("customer_address","") ?: "")}
 var detectedLocation by remember{mutableStateOf(prefs.getString("detected_location","") ?: "")}
 var deliveryDistanceKm by remember{mutableDoubleStateOf(0.0)}
 var distanceCalculating by remember { mutableStateOf(false) }
 var profileGateMessage by remember { mutableStateOf("") }
 var locationMessage by remember{mutableStateOf("")}
 var currentLat by remember { mutableStateOf(prefs.getString("current_lat",null)?.toDoubleOrNull()) }
 var currentLon by remember { mutableStateOf(prefs.getString("current_lon",null)?.toDoubleOrNull()) }
 var showPermissionIntro by remember { mutableStateOf(!prefs.getBoolean("permission_intro_seen",false)) }
 var showAddressEditor by remember { mutableStateOf(false) }
 var showLocationPicker by remember { mutableStateOf(false) }
 var editProfile by remember { mutableStateOf(false) }
 var editingAddressLabel by remember { mutableStateOf("Home") }
 var editingAddressText by remember { mutableStateOf("") }
 var editingLandmark by remember { mutableStateOf("") }
 var editingAddressType by remember { mutableStateOf("Other") }
 var addressBook by remember { mutableStateOf(runCatching { val a=JSONArray(prefs.getString("address_book","[]")); (0 until a.length()).map { i -> val o=a.getJSONObject(i); SavedAddress(o.optString("label"),o.optString("details"),o.optString("landmark"),o.optString("addressType","Other"),if(o.isNull("lat")) null else o.optDouble("lat"),if(o.isNull("lon")) null else o.optDouble("lon")) } }.getOrDefault(emptyList())) }
 val notificationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> locationMessage = if(granted) "Notifications enabled." else "Notifications are off; you can still use the app." }
 val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
   if (grants[Manifest.permission.ACCESS_FINE_LOCATION] == true || grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
     try { val lm=context.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
       val loc=listOf(LocationManager.GPS_PROVIDER,LocationManager.NETWORK_PROVIDER).mapNotNull { p -> runCatching { if(lm.isProviderEnabled(p)) lm.getLastKnownLocation(p) else null }.getOrNull() }.maxByOrNull { it.time }
       if(loc!=null){ currentLat=loc.latitude; currentLon=loc.longitude; detectedLocation=runCatching { Geocoder(context,Locale.getDefault()).getFromLocation(loc.latitude,loc.longitude,1)?.firstOrNull()?.getAddressLine(0) }.getOrNull() ?: "%.6f, %.6f".format(Locale.US,loc.latitude,loc.longitude); address=detectedLocation; locationMessage="Location updated"; prefs.edit().putString("current_lat",loc.latitude.toString()).putString("current_lon",loc.longitude.toString()).apply() }
       else locationMessage="Location permission granted. Turn on device Location and tap Detect again."
     } catch(e:Exception){locationMessage="Could not resolve address. Enter it manually."}
   } else locationMessage="Location permission not granted. You can still enter an address manually."
   prefs.edit().putBoolean("permission_intro_seen",true).apply()
   if(Build.VERSION.SDK_INT>=33 && ContextCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED) notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
 }

 var orderHistory by remember{mutableStateOf(runCatching { val a=JSONArray(prefs.getString("orders","[]")); (0 until a.length()).map { i -> val o=a.getJSONObject(i); OrderRecord(o.optString("id",o.optString("description")),o.optInt("total"),o.optString("description"),o.optString("customerName",prefs.getString("customer_name","") ?: ""),o.optString("phone",prefs.getString("customer_phone","") ?: ""),o.optString("address",prefs.getString("customer_address","") ?: ""),o.optString("fulfillment","Delivery"),o.optString("paymentMethod","Unknown"),o.optString("itemsJson","[]"),o.optInt("itemTotal",o.optInt("total")),o.optInt("deliveryFee"),o.optInt("packagingFee"),o.optDouble("distanceKm"),o.optString("preferences","No preference"),o.optString("status","Order received"),o.optLong("createdAt")) } }.getOrDefault(emptyList()))}
 var selectedOrder by remember{mutableStateOf<OrderRecord?>(null)}
 var lastOrder by remember{mutableStateOf(false)}
 var fulfillment by remember{mutableStateOf("Delivery")}
 var paymentMethod by remember{mutableStateOf("Online")}
 var orderNumber by remember{mutableStateOf("LBFP-0001")}
 var preferences by remember{mutableStateOf(prefs.getString("food_preferences","No onions") ?: "No onions") }
 LaunchedEffect(address, fulfillment, addressBook) {
   if (fulfillment != "Delivery" || address.isBlank()) {
     deliveryDistanceKm = 0.0
     distanceCalculating = false
   } else {
     distanceCalculating = true
     val saved = addressBook.firstOrNull { it.details == address }
     val result = withContext(Dispatchers.IO) {
       runCatching {
         val geocoder = Geocoder(context, Locale.getDefault())
         val destination = if (saved?.latitude != null && saved.longitude != null) {
           android.location.Address(Locale.getDefault()).apply { latitude = saved.latitude!!; longitude = saved.longitude!! }
         } else if (address == detectedLocation && currentLat != null && currentLon != null) {
           android.location.Address(Locale.getDefault()).apply { latitude = currentLat!!; longitude = currentLon!! }
         } else {
           geocoder.getFromLocationName(address, 1)?.firstOrNull()
         }
         val restaurant = geocoder.getFromLocationName(RESTAURANT_ADDRESS, 1)?.firstOrNull()
         if (destination == null || restaurant == null) null else {
           val routeUrl = URL(
             "https://router.project-osrm.org/route/v1/driving/" +
               "${restaurant.longitude},${restaurant.latitude};${destination.longitude},${destination.latitude}?overview=false"
           )
           val connection = (routeUrl.openConnection() as HttpURLConnection).apply {
             requestMethod = "GET"
             connectTimeout = 8000
             readTimeout = 8000
             setRequestProperty("User-Agent", "LBFP-Android/1.0")
           }
           try {
             if (connection.responseCode != HttpURLConnection.HTTP_OK) null
             else {
               val routeJson = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
               val route = routeJson.optJSONArray("routes")?.optJSONObject(0)
               val meters = route?.optDouble("distance", 0.0) ?: 0.0
               if (meters > 0.0) meters / 1000.0 else null
             }
           } finally {
             connection.disconnect()
           }
         }
       }.getOrNull()
     }
     deliveryDistanceKm = result ?: 0.0
     distanceCalculating = false
     locationMessage = if (result == null) "Unable to calculate a driving route for this address. Check the saved address and try again." else "Driving distance calculated automatically."
   }
 }
 LaunchedEffect(name, phone) {
   if (name.isNotBlank() && phone.matches(Regex("^\\d{10}$"))) profileGateMessage = ""
 }
 LaunchedEffect(cart,name,phone,address,preferences,orderHistory) {
   val savedCart=JSONArray()
   cart.values.forEach { l -> savedCart.put(JSONObject().put("name",l.food.name).put("variant",l.variant).put("price",l.price).put("qty",l.qty)) }
   val savedOrders=JSONArray()
   orderHistory.forEach { o -> savedOrders.put(JSONObject().put("id",o.id).put("total",o.total).put("description",o.description).put("customerName",o.customerName).put("phone",o.phone).put("address",o.address).put("fulfillment",o.fulfillment).put("paymentMethod",o.paymentMethod).put("itemsJson",o.itemsJson).put("itemTotal",o.itemTotal).put("deliveryFee",o.deliveryFee).put("packagingFee",o.packagingFee).put("distanceKm",o.distanceKm).put("preferences",o.preferences).put("status",o.status).put("createdAt",o.createdAt)) }
   prefs.edit().putString("cart",savedCart.toString()).putString("orders",savedOrders.toString())
     .putString("customer_name",name).putString("customer_phone",phone)
     .putString("customer_address",address).putString("detected_location",detectedLocation).putString("food_preferences",preferences).putString("address_book",JSONArray().apply { addressBook.forEach { a -> put(JSONObject().put("label",a.label).put("details",a.details).put("landmark",a.landmark).put("addressType",a.addressType).put("lat",a.latitude).put("lon",a.longitude)) } }.toString()).apply()
 }
 val count=cart.values.sumOf{it.qty}
 val total=cart.values.sumOf{it.qty*it.price}
 val deliveryFee=if(fulfillment!="Delivery"||deliveryDistanceKm<=0.0) 0 else if(deliveryDistanceKm<1.0) 10 else (kotlin.math.ceil(kotlin.math.min(deliveryDistanceKm,5.0)*8.0)+kotlin.math.ceil(kotlin.math.max(deliveryDistanceKm-5.0,0.0)*10.0)).toInt()
 val packagingFee=if(cart.isEmpty()) 0 else 10
 val grandTotal=total+(if(fulfillment=="Delivery") deliveryFee else 0)+packagingFee
 MaterialTheme(colorScheme=if(darkMode) darkColorScheme(primary=Gold,secondary=Gold,background=Cream,surface=CardColor,onBackground=Ink,onSurface=Ink) else lightColorScheme(primary=Ink,secondary=Gold,background=Cream,surface=Color.White)){
 if(showPermissionIntro) AlertDialog(onDismissRequest={showPermissionIntro=false;prefs.edit().putBoolean("permission_intro_seen",true).apply()},title={Text("A better ordering experience")},text={Column(verticalArrangement=Arrangement.spacedBy(10.dp)){Text("Location: shows your current address and saved delivery places, and helps calculate distance from the restaurant.");Text("Notifications: can show order and service updates when those features are available. You can still use the app if you decline.")}},confirmButton={TextButton(onClick={showPermissionIntro=false;prefs.edit().putBoolean("permission_intro_seen",true).apply();locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION))}){Text("Continue")}},dismissButton={TextButton(onClick={showPermissionIntro=false;prefs.edit().putBoolean("permission_intro_seen",true).apply()}){Text("Not now")}})
 BackHandler(enabled=selectedOrder!=null||selectedFood!=null||cartOpen||checkout||lastOrder||tab!="Home") { when { selectedOrder!=null -> selectedOrder=null; selectedFood!=null -> selectedFood=null; checkout -> { checkout=false; cartOpen=true }; cartOpen -> cartOpen=false; lastOrder -> { lastOrder=false; tab="Home" }; tab!="Home" -> tab="Home" } }
 Scaffold(containerColor=Cream,topBar={
 TopAppBar(title={Column{
 Text("Laddu Bhai Food Point",fontSize=18.sp,fontWeight=FontWeight.ExtraBold,color=Ink)
  Text(if(detectedLocation.isNotBlank()) "⌖  ${detectedLocation.take(48)}" else "⌖  Tap to detect your current location",fontSize=10.sp,color=Muted,letterSpacing=.2.sp,maxLines=1,overflow=TextOverflow.Ellipsis,modifier=Modifier.clickable { showLocationPicker=true })
 }},actions={IconButton(onClick={cartOpen=true}){BadgedBox(badge={if(count>0)Badge{Text("$count")}}){Icon(Icons.Default.ShoppingBag,contentDescription="Cart",tint=Ink)}}},colors=TopAppBarDefaults.topAppBarColors(containerColor=Cream))
 },bottomBar={
 if(!cartOpen&&!checkout&&!lastOrder&&selectedFood==null) Column {
 if(count>0 && (tab=="Home"||tab=="Menu")) Surface(color=Color(0xFF168044),shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth().padding(horizontal=12.dp,vertical=6.dp).clickable{cartOpen=true}) {
  Row(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=12.dp),verticalAlignment=Alignment.CenterVertically){
   Column(Modifier.weight(1f)){Text("$count item${if(count==1) "" else "s"}  |  ₹$total",fontSize=14.sp,fontWeight=FontWeight.Bold,color=Color.White);Text("Food subtotal · delivery & packaging shown at checkout",fontSize=10.sp,color=Color.White)}
   Text("View cart  →",fontSize=14.sp,fontWeight=FontWeight.ExtraBold,color=Color.White)
  }
 }
 if(!checkout && !cartOpen && !lastOrder) NavigationBar(containerColor=Color.White){
 NavigationBarItem(selected=tab=="Home",onClick={tab="Home"},icon={Icon(Icons.Default.Home,null)},label={Text("Home")})
 NavigationBarItem(selected=tab=="Menu",onClick={tab="Menu"},icon={Icon(Icons.Default.MenuBook,null)},label={Text("Menu")})
 NavigationBarItem(selected=tab=="Orders",onClick={tab="Orders"},icon={Icon(Icons.Default.ReceiptLong,null)},label={Text("Orders")})
 NavigationBarItem(selected=tab=="Profile"||tab=="Help",onClick={tab="Profile"},icon={Icon(Icons.Default.Settings,null)},label={Text("Settings")})
 }
 }
 }){padding->
 when{
 selectedOrder!=null -> OrderDetailPage(selectedOrder!!, onBack={selectedOrder=null})
 selectedFood!=null->FoodDetailPage(selectedFood!!,cart,{f,v,p->changeQty(cart,{cart=it},Line(f,v,p,cart[itemKey(f,v)]?.qty?:0),1)},{f,v,p,q->setLineQty(cart,{cart=it},Line(f,v,p,cart[itemKey(f,v)]?.qty?:0), (cart[itemKey(f,v)]?.qty?:0)+q)},{selectedFood=null},{f,v->cart[itemKey(f,v)]?.let{changeQty(cart,{cart=it},it,-1)}})
 lastOrder->Column(Modifier.padding(padding).fillMaxSize().padding(26.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
 Box(Modifier.size(78.dp).background(Gold,CircleShape),contentAlignment=Alignment.Center){Text("✓",fontSize=42.sp,fontWeight=FontWeight.Black)}
 Spacer(Modifier.height(18.dp));Text("Order placed!",fontSize=27.sp,fontWeight=FontWeight.ExtraBold,color=Ink)
 Text(orderNumber,fontSize=13.sp,fontWeight=FontWeight.Bold,color=Color(0xFFFC8019))
 Text("Your order summary",color=Muted)
 Spacer(Modifier.height(14.dp))
 Surface(color=Color.White,shape=RoundedCornerShape(20.dp)){Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
 Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(36.dp).background(Color(0xFFFFF0E5),CircleShape),contentAlignment=Alignment.Center){Text("1",fontWeight=FontWeight.Bold,color=Color(0xFF18794E))};Spacer(Modifier.width(10.dp));Column{Text("Order received",fontWeight=FontWeight.Bold);Text("Waiting for restaurant confirmation",fontSize=12.sp,color=Muted)}}
 Divider(color=Color(0xFFECEBE5))
 Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(36.dp).background(Cream,CircleShape),contentAlignment=Alignment.Center){Text("2",fontWeight=FontWeight.Bold,color=Muted)};Spacer(Modifier.width(10.dp));Column{Text("Preparing your food",fontWeight=FontWeight.Bold);Text("Status updates are not live in this demo",fontSize=12.sp,color=Muted)}}
 Text("$fulfillment · $paymentMethod",fontSize=12.sp,color=Muted)
 }}
 Spacer(Modifier.height(12.dp));Text("Total · ₹$grandTotal",fontSize=22.sp,fontWeight=FontWeight.Bold)
 Text("Demo only: this order has not been sent to the restaurant. No live tracking or payment is connected.",fontSize=12.sp,color=Muted)
 Spacer(Modifier.height(18.dp));Button(onClick={cart=emptyMap();lastOrder=false;tab="Home"},colors=ButtonDefaults.buttonColors(containerColor=Ink)){Text("Continue exploring",color=Gold)}
 }
 checkout -> Column(
     Modifier.padding(padding).fillMaxSize().imePadding().navigationBarsPadding()
 ) {
     LazyColumn(
         Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
         contentPadding = PaddingValues(top = 12.dp, bottom = 18.dp),
         verticalArrangement = Arrangement.spacedBy(14.dp)
     ) {
         item {
             Row(verticalAlignment = Alignment.CenterVertically) {
                 
                 Column {
                     Text("CHECKOUT", fontSize = 10.sp, color = Color(0xFF18794E), fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                     Text("Complete your order", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                 }
             }
         }
         item {
             Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
                 Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                     Text("Order Summary", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                     cart.values.forEach { line ->
                         Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                             Column(Modifier.weight(1f)) {
                                 Text(line.food.name, fontWeight = FontWeight.SemiBold, color = Ink)
                                 Text("Qty ${line.qty} · ${line.variant}", fontSize = 12.sp, color = Muted)
                             }
                             Text("₹${line.price * line.qty}", fontWeight = FontWeight.Bold, color = Ink)
                         }
                     }
                     Divider(color = Color(0xFFE8E7E0))
                     Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                         Text("Item total", color = Muted)
                         Text("₹$total", fontWeight = FontWeight.SemiBold, color = Ink)
                     }
                     Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                         Text("Packaging fee", color = Muted)
                         Text("₹$packagingFee", fontWeight = FontWeight.SemiBold, color = Ink)
                     }
                     if (fulfillment == "Delivery") {
                         Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                             Text("Delivery fee", color = Muted)
                             Text(if (distanceCalculating) "Calculating…" else if (deliveryDistanceKm > 0.0) "₹$deliveryFee · ${"%.1f".format(Locale.US, deliveryDistanceKm)} km" else "Calculating address…", fontWeight = FontWeight.SemiBold, color = Ink)
                         }
                     }
                     Divider(color = Color(0xFFE8E7E0))
                     Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                         Text("Total", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Ink)
                         Text("₹$grandTotal", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF18794E))
                     }
                 }
             }
         }
         item {
             Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
                 Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                     Text("Delivery Details", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                     Column(Modifier.fillMaxWidth()) {
                         Text("Customer name", fontSize = 12.sp, color = Muted)
                         Text(name.ifBlank { "Add your name in Settings" }, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                     }
                     Column(Modifier.fillMaxWidth()) {
                         Text("Mobile number", fontSize = 12.sp, color = Muted)
                         Text(phone.ifBlank { "Add your mobile number in Settings" }, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                     }
                     if (fulfillment == "Delivery") {
                         Column(Modifier.fillMaxWidth()) {
                             Text("Exact delivery address", fontSize = 12.sp, color = Muted)
                             val selectedSaved = addressBook.firstOrNull { it.details == address }
                             Text(address.ifBlank { "Choose an address from your saved locations" }, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Ink)
                             if (selectedSaved?.landmark?.isNotBlank() == true) Text("Landmark: ${selectedSaved.landmark}", fontSize = 12.sp, color = Muted)
                             if (selectedSaved != null) Text("Address type: ${selectedSaved.addressType}", fontSize = 12.sp, color = Muted)
                         }
                         Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                             TextButton(onClick = { showLocationPicker = true }) { Text("Choose saved address") }
                             TextButton(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(RESTAURANT_MAP_URL))) }) { Text("Restaurant map ↗") }
                         }
                         if (addressBook.isNotEmpty()) {
                             Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                 addressBook.forEach { saved ->
                                     FilterChip(
                                         selected = address == saved.details,
                                         onClick = { address = saved.details; detectedLocation = saved.details; currentLat = saved.latitude; currentLon = saved.longitude },
                                         label = { Text(saved.label, maxLines = 1) }
                                     )
                                 }
                             }
                         }
                         if (locationMessage.isNotBlank()) Text(locationMessage, fontSize = 12.sp, color = Muted)
                         Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                             Text("Delivery distance", fontSize = 13.sp, color = Muted)
                             Text(if (distanceCalculating) "Calculating…" else if (deliveryDistanceKm > 0.0) "${"%.1f".format(Locale.US, deliveryDistanceKm)} km" else "Unavailable", fontWeight = FontWeight.SemiBold, color = Ink)
                         }
                         Text("Driving route and delivery fee are calculated automatically in the background.", fontSize = 11.sp, color = Muted)
                     }
                 }
             }
         }
         item {
             Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
                 Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                     Text("Order type", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                     Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                         listOf("Delivery", "Pickup").forEach { choice ->
                             Surface(
                                 color = if (fulfillment == choice) Color(0xFF18794E) else Color(0xFFF7F7F2),
                                 shape = RoundedCornerShape(13.dp),
                                 modifier = Modifier.weight(1f).clickable { fulfillment = choice; paymentMethod = if (choice == "Delivery") "Online" else "At Restaurant" }
                             ) {
                                 Text(choice, modifier = Modifier.padding(14.dp), color = if (fulfillment == choice) Color.White else Ink, fontWeight = FontWeight.Bold)
                             }
                         }
                     }
                     if (fulfillment == "Pickup") Text("Collect your order at the restaurant.", fontSize = 13.sp, color = Muted)
                     Text("Payment mode", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink)
                     (if (fulfillment == "Delivery") listOf("Online", "Cash on Delivery") else listOf("Online", "Cash on Delivery")).forEach { choice ->
                         Surface(
                             color = if (paymentMethod == choice) Color(0xFFE4F1E8) else Color(0xFFF8F8F4),
                             shape = RoundedCornerShape(12.dp),
                             modifier = Modifier.fillMaxWidth().clickable { paymentMethod = choice }
                         ) {
                             Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                 RadioButton(
                                     selected = paymentMethod == choice,
                                     onClick = { paymentMethod = choice },
                                     colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF18794E))
                                 )
                                 Spacer(Modifier.width(8.dp))
                                 Text(choice, color = Ink, fontWeight = FontWeight.Medium)
                             }
                         }
                     }
                 }
             }
         }
         item {
             Text("No platform fee or service fee. Food prices match the dine-in menu.", fontSize = 12.sp, color = Muted, modifier = Modifier.padding(bottom = 8.dp))
         }
     }
     Surface(color = Color.White, shadowElevation = 8.dp) {
         Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
             Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                 Column {
                     Text("Total payable", fontSize = 12.sp, color = Muted)
                     Text("₹$grandTotal", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                 }
                 Text(if (fulfillment == "Delivery") "Delivery" else "Pickup", color = Color(0xFF18794E), fontWeight = FontWeight.Bold)
             }
             Button(
                 onClick = {
                     val datePrefix = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyy", Locale.US))
                     val dailySequence = ((orderHistory.mapNotNull { it.id.takeIf { id -> id.matches(Regex("[0-9]{8,}.*")) } }.filter { it.id.take(6) == datePrefix }.maxOfOrNull { it.id.drop(6).takeWhile { ch -> ch.isDigit() }.toIntOrNull() ?: 0 }) ?: 0) + 1
                     val typeCode = when { fulfillment == "Delivery" && paymentMethod == "Online" -> "DO"; fulfillment == "Delivery" -> "DC"; paymentMethod == "Online" -> "PO"; else -> "PC" }
                     val secureRandom = java.security.SecureRandom()
                     var generatedId: String
                     do { generatedId = datePrefix + dailySequence.toString().padStart(2, '0') + typeCode + (1000 + secureRandom.nextInt(9000)).toString() } while (orderHistory.any { it.id == generatedId })
                     orderNumber = generatedId
                     val orderItems = JSONArray().apply { cart.values.forEach { line -> put(JSONObject().put("name",line.food.name).put("variant",line.variant).put("price",line.price).put("qty",line.qty).put("lineTotal",line.price*line.qty)) } }.toString()
                     orderHistory = listOf(OrderRecord(generatedId,grandTotal,"Order placed · " + generatedId,name,phone,address,fulfillment,paymentMethod,orderItems,total,if(fulfillment=="Delivery") deliveryFee else 0,packagingFee,deliveryDistanceKm,preferences,"Order received",System.currentTimeMillis())) + orderHistory + orderHistory
                     checkout = false
                     cartOpen = false
                     lastOrder = true
                 },
                 enabled = name.isNotBlank() && phone.matches(Regex("^\\d{10}$")) && cart.isNotEmpty() && !distanceCalculating && (fulfillment == "Pickup" || (address.isNotBlank() && deliveryDistanceKm > 0.0)),
                 modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                 colors = ButtonDefaults.buttonColors(containerColor = Ink)
             ) {
                 Text("Place order · ₹$grandTotal", color = Gold, fontWeight = FontWeight.Bold)
             }
         }
     }
 }
 cartOpen->Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)){
 Row(verticalAlignment=Alignment.CenterVertically){Text("Your bag",fontSize=26.sp,fontWeight=FontWeight.ExtraBold,color=Ink)}
 if(cart.isEmpty())Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Icon(Icons.Default.ShoppingBag,null,tint=Gold,modifier=Modifier.size(46.dp));Text("Your bag is waiting",fontSize=21.sp,fontWeight=FontWeight.Bold);Text("Add a favourite from our menu",color=Muted);Button(onClick={cartOpen=false;tab="Menu"}){Text("Explore menu")}}}
 else{
 LazyColumn(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(9.dp)){items(cart.values.toList(),key={itemKey(it.food,it.variant)}){line->Surface(color=Color.White,shape=RoundedCornerShape(16.dp)){Row(Modifier.fillMaxWidth().padding(13.dp),verticalAlignment=Alignment.CenterVertically){AsyncImage(model=foodPhoto(line.food),contentDescription=line.food.name,contentScale=ContentScale.Crop,modifier=Modifier.size(76.dp).clip(RoundedCornerShape(14.dp)));Spacer(Modifier.width(12.dp));Column(Modifier.weight(1f)){Text(line.food.name,fontWeight=FontWeight.Bold,color=Ink);Text("${line.variant} · ₹${line.price} each",fontSize=12.sp,color=Muted);Text("₹${line.price*line.qty}",fontWeight=FontWeight.ExtraBold,color=Ink)};IconButton(onClick={changeQty(cart,{cart=it},line,-1)}){Icon(Icons.Default.Remove,null)};Text("${line.qty}",fontWeight=FontWeight.Bold);IconButton(onClick={changeQty(cart,{cart=it},line,1)}){Icon(Icons.Default.Add,null)}}}}}
 Surface(color=Color.White,shape=RoundedCornerShape(18.dp)){Column(Modifier.padding(16.dp)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("Item subtotal",fontSize=14.sp);Text("₹$total",fontSize=20.sp,fontWeight=FontWeight.ExtraBold)};Spacer(Modifier.height(10.dp));Button(onClick={if(name.isBlank() || !phone.matches(Regex("^\\d{10}$"))){cartOpen=false;checkout=false;tab="Profile";editProfile=true;profileGateMessage="Add your full name and valid 10-digit mobile number to continue checkout."}else{cartOpen=false;checkout=true}},modifier=Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Ink)){Text("Continue to checkout",color=Gold)}}}
 }
 }
 tab=="Orders"->Column(Modifier.padding(padding).fillMaxSize().padding(20.dp)){
 Text("My Orders",fontSize=28.sp,fontWeight=FontWeight.ExtraBold,color=Ink);Text("Every order, all in one place",color=Muted)
 Spacer(Modifier.height(16.dp))
 Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf("All","Ongoing","Completed").forEach{filter->FilterChip(selected=orderFilter==filter,onClick={orderFilter=filter},label={Text(filter)},colors=FilterChipDefaults.filterChipColors(selectedContainerColor=Color(0xFF18794E),selectedLabelColor=Color.White))}}
 Spacer(Modifier.height(10.dp))
 if(orderHistory.isEmpty())Surface(color=Color.White,shape=RoundedCornerShape(20.dp)){Column(Modifier.fillMaxWidth().padding(22.dp)){Icon(Icons.Default.ReceiptLong,null,tint=Gold,modifier=Modifier.size(36.dp));Spacer(Modifier.height(8.dp));Text("No orders yet",fontSize=20.sp,fontWeight=FontWeight.Bold);Text("Your next favourite meal starts here.",color=Muted);Button(onClick={tab="Menu"}){Text("Browse menu")}}}
 else LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp)){items(orderHistory){o->Surface(color=CardColor,shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth().clickable { selectedOrder=o }){Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(5.dp)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){Text(o.id,fontWeight=FontWeight.ExtraBold,color=Ink);Text(o.status,fontSize=11.sp,color=Color(0xFF18794E),fontWeight=FontWeight.Bold)};Text("Laddu Bhai Food Point",color=Muted);Text("₹${o.total}",fontWeight=FontWeight.ExtraBold,fontSize=20.sp);Text("${o.fulfillment} · ${o.paymentMethod}",fontSize=12.sp,color=Muted);Text("Tap to view full order details  →",fontSize=12.sp,color=Gold,fontWeight=FontWeight.Bold)}}}}
 }
 tab=="Help"->Column(Modifier.padding(padding).fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text("Help & support",fontSize=28.sp,fontWeight=FontWeight.ExtraBold,color=Ink);Text("Quick answers for your order",color=Muted);listOf("Where is my order?","Orders are saved on this device only. Live restaurant confirmation and tracking are not connected.","How do I change an item?","Open the cart and use +, −, or edit the quantity field. You can also return to the menu to change serving size.","How does payment work?","Payment is not processed in this demo. Confirm payment options directly with the restaurant.","Need help with a bulk order?","Please contact Laddu Bhai Food Point directly; contact details are not configured in this app.").chunked(2).forEach{pair->Surface(color=Color.White,shape=RoundedCornerShape(14.dp)){Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){Text(pair[0],fontWeight=FontWeight.Bold,color=Ink);Text(pair[1],fontSize=13.sp,color=Muted)}}};Button(onClick={tab="Profile"},modifier=Modifier.fillMaxWidth()){Text("Back to profile")}}
 tab=="Profile"->Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 Text("Settings",fontSize=29.sp,fontWeight=FontWeight.ExtraBold,color=Ink);Text("Your account, saved places and app preferences",color=Muted)
 Text("Appearance",fontSize=19.sp,fontWeight=FontWeight.Bold,color=Ink)
 Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) { listOf(Triple("System",Icons.Default.SettingsBrightness,"system"),Triple("Light",Icons.Default.LightMode,"light"),Triple("Dark",Icons.Default.DarkMode,"dark")).forEach { (label,icon,mode) -> FilterChip(selected=themeMode==mode,onClick={themeMode=mode;prefs.edit().putString("theme_mode",mode).apply()},label={Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(5.dp)){Icon(icon,contentDescription=label,modifier=Modifier.size(17.dp));Text(label)}},colors=FilterChipDefaults.filterChipColors(selectedContainerColor=Gold,selectedLabelColor=Color.White)) } }
 if(profileGateMessage.isNotBlank())Text(profileGateMessage,fontSize=13.sp,color=Color(0xFFB3261E),fontWeight=FontWeight.SemiBold);
 Surface(color=Ink,shape=RoundedCornerShape(22.dp)){Column(Modifier.fillMaxWidth().padding(20.dp)){Text("LADDU BHAI FOOD POINT",fontSize=11.sp,color=Gold,fontWeight=FontWeight.Bold);Text(if(name.isBlank()) "Your profile" else name,fontSize=24.sp,color=Color.White,fontWeight=FontWeight.ExtraBold);Text(if(phone.isBlank()) "Add your mobile number below" else phone,color=Color(0xFFE0EAE3),fontSize=13.sp)}}
 Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Text("Personal details",fontSize=19.sp,fontWeight=FontWeight.Bold,color=Ink);Spacer(Modifier.weight(1f));TextButton(onClick={editProfile=!editProfile}){Text(if(editProfile)"Done editing" else "Edit profile")}}
 if(editProfile){OutlinedTextField(name,{name=it},label={Text("Full name")},modifier=Modifier.fillMaxWidth(),singleLine=true);OutlinedTextField(phone,{phone=it.filter{ch->ch.isDigit()}.take(10)},label={Text("Mobile number")},modifier=Modifier.fillMaxWidth(),singleLine=true)}else{Surface(color=Color.White,shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth()){Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){Text(name.ifBlank{"Name not added"},fontWeight=FontWeight.SemiBold,color=Ink);Text(phone.ifBlank{"Mobile number not added"},fontSize=13.sp,color=Muted)}}}
 Text("Address book",fontSize=21.sp,fontWeight=FontWeight.ExtraBold,color=Ink);Text("Save any place with your own label, address type and landmark.",fontSize=12.sp,color=Muted)
 addressBook.forEach { saved -> Surface(color=Color.White,shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth()){Row(Modifier.padding(12.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(saved.label,fontWeight=FontWeight.Bold,color=Ink);Text(saved.details + if(saved.landmark.isNotBlank()) " · Near ${saved.landmark}" else "",fontSize=12.sp,color=Muted,maxLines=2)};TextButton(onClick={address=saved.details;detectedLocation=saved.details;currentLat=saved.latitude;currentLon=saved.longitude}){Text("Use")};TextButton(onClick={editingAddressLabel=saved.label;editingAddressType=saved.addressType;editingLandmark=saved.landmark;editingAddressText=saved.details;showAddressEditor=true}){Text("Edit")};TextButton(onClick={addressBook=addressBook.filterNot{it.label==saved.label}}){Text("Delete",color=Color(0xFFB3261E))}}} }
 Surface(color=Color.White,shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth().clickable{tab="Orders"}){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.ReceiptLong,null,tint=Ink);Spacer(Modifier.width(10.dp));Column(Modifier.weight(1f)){Text("Order history",fontWeight=FontWeight.Bold,color=Ink);Text("Orders saved on this device",fontSize=12.sp,color=Muted)};Text("→",color=Ink,fontSize=20.sp)}}
 Surface(color=Color.White,shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth().clickable{tab="Help"}){Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.Person,null,tint=Ink);Spacer(Modifier.width(10.dp));Column(Modifier.weight(1f)){Text("Help & support",fontWeight=FontWeight.Bold,color=Ink);Text("FAQs, order and payment help",fontSize=12.sp,color=Muted)};Text("→",color=Ink,fontSize=20.sp)}}
 Text("Food preferences",fontSize=19.sp,fontWeight=FontWeight.Bold,color=Ink)
 listOf("No onions","Less spicy","Extra spicy","No preference").forEach{choice->Surface(color=Color.White,shape=RoundedCornerShape(12.dp),modifier=Modifier.fillMaxWidth().clickable{preferences=choice}){Row(Modifier.padding(horizontal=12.dp,vertical=2.dp),verticalAlignment=Alignment.CenterVertically){RadioButton(selected=preferences==choice,onClick={preferences=choice},colors=RadioButtonDefaults.colors(selectedColor=Color(0xFF18794E)));Text(choice,color=Ink)}}}
 Text("Permissions",fontSize=19.sp,fontWeight=FontWeight.Bold,color=Ink)
 Surface(color=Color.White,shape=RoundedCornerShape(14.dp)){Column(Modifier.padding(14.dp)){Text("Location",fontWeight=FontWeight.Bold,color=Ink);Text("Used to detect your address and choose saved delivery locations.",fontSize=12.sp,color=Muted);TextButton(onClick={locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION))}){Text("Grant / refresh location permission")};Text("Notifications",fontWeight=FontWeight.Bold,color=Ink);Text("Used for order updates when supported.",fontSize=12.sp,color=Muted);TextButton(onClick={if(Build.VERSION.SDK_INT>=33)notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)else locationMessage="This Android version does not require notification runtime permission."}){Text("Manage notification permission")};if(locationMessage.isNotBlank())Text(locationMessage,fontSize=12.sp,color=Muted)}}
 }
 else->HomePage(padding,tab,category,{category=it;tab="Menu"},cart,
 onAdd={f,v,price->changeQty(cart,{cart=it},Line(f,v,price,cart[itemKey(f,v)]?.qty?:0),1)},
 onRemove={f,v->val l=cart[itemKey(f,v)]?:return@HomePage;changeQty(cart,{cart=it},l,-1)},
 onMenu={tab="Menu"},
 filterMenu=tab=="Menu",onFoodClick={selectedFood=it})
 }
 }
 }

 if(showLocationPicker) AlertDialog(onDismissRequest={showLocationPicker=false},title={Text("Choose delivery location")},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){Text("Use your current precise location or select a saved place.");Button(onClick={showLocationPicker=false;if(ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){val lm=context.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager;val loc=listOf(LocationManager.GPS_PROVIDER,LocationManager.NETWORK_PROVIDER).mapNotNull{p->runCatching{if(lm.isProviderEnabled(p))lm.getLastKnownLocation(p)else null}.getOrNull()}.maxByOrNull{it.time};if(loc!=null){currentLat=loc.latitude;currentLon=loc.longitude;detectedLocation=runCatching{Geocoder(context,Locale.getDefault()).getFromLocation(loc.latitude,loc.longitude,1)?.firstOrNull()?.getAddressLine(0)}.getOrNull()?: "%.6f, %.6f".format(Locale.US,loc.latitude,loc.longitude);address=detectedLocation;locationMessage="Location updated";prefs.edit().putString("current_lat",loc.latitude.toString()).putString("current_lon",loc.longitude.toString()).apply()}else locationMessage="Turn on device Location and retry"}else locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION))},modifier=Modifier.fillMaxWidth()){Text("Use current location")};addressBook.forEach{saved->Surface(color=Color(0xFFF8F7F2),shape=RoundedCornerShape(12.dp),modifier=Modifier.fillMaxWidth().clickable{address=saved.details;detectedLocation=saved.details;currentLat=saved.latitude;currentLon=saved.longitude;showLocationPicker=false}){Column(Modifier.padding(12.dp)){Text(saved.label,fontWeight=FontWeight.Bold,color=Ink);Text(saved.details,fontSize=12.sp,color=Muted)}}};TextButton(onClick={showLocationPicker=false;showAddressEditor=true;editingAddressLabel="";editingAddressType="Other";editingLandmark="";editingAddressText=address}){Text("+ Add a saved address")}}},confirmButton={TextButton(onClick={showLocationPicker=false}){Text("Done")}})
 if(showAddressEditor) AlertDialog(onDismissRequest={showAddressEditor=false},title={Text("Save address")},text={Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(10.dp)){OutlinedTextField(value=editingAddressLabel,onValueChange={editingAddressLabel=it},label={Text("Address label (e.g. Apartment, Parents, Gym)")},singleLine=true,modifier=Modifier.fillMaxWidth());OutlinedTextField(value=editingAddressType,onValueChange={editingAddressType=it},label={Text("Address type (optional)")},singleLine=true,modifier=Modifier.fillMaxWidth());OutlinedTextField(value=editingAddressText,onValueChange={editingAddressText=it},label={Text("Exact address")},minLines=3,modifier=Modifier.fillMaxWidth());OutlinedTextField(value=editingLandmark,onValueChange={editingLandmark=it},label={Text("Landmark (optional)")},singleLine=true,modifier=Modifier.fillMaxWidth())}},confirmButton={TextButton(onClick={if(editingAddressLabel.isNotBlank()&&editingAddressText.isNotBlank()){addressBook=addressBook.filterNot{it.label.equals(editingAddressLabel,true)}+SavedAddress(editingAddressLabel.trim(),editingAddressText.trim(),editingLandmark.trim(),editingAddressType.trim().ifBlank{"Other"},if(editingAddressText==detectedLocation)currentLat else null,if(editingAddressText==detectedLocation)currentLon else null);address=editingAddressText.trim();showAddressEditor=false}}){Text("Save address")}},dismissButton={TextButton(onClick={showAddressEditor=false}){Text("Cancel")}})
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomePage(
    padding: PaddingValues,
    tab: String,
    category: String,
    onCategory: (String) -> Unit,
    cart: Map<String, Line>,
    onAdd: (Food, String, Int) -> Unit,
    onRemove: (Food, String) -> Unit,
    onMenu: () -> Unit,
    filterMenu: Boolean,
    onFoodClick: (Food) -> Unit
) {
    var search by remember { mutableStateOf("") }
    
    val categories = listOf("ALL", "NON-VEG", "VEG", "ROTI")
    val filtered = menu.filter { f ->
        (category == "ALL" || f.category == category) &&
        
        (search.isBlank() || f.name.contains(search.trim(), ignoreCase = true))
    }
    LazyColumn(
        modifier = Modifier.padding(padding).fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp).padding(top = 12.dp, bottom = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(if (filterMenu) "LADDU BHAI FOOD POINT" else "GOOD FOOD. GOOD MOOD.", fontSize = 10.sp, color = Muted, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp)
                        Spacer(Modifier.height(5.dp))
                        Text(if (filterMenu) "Explore menu" else "What are you craving?", fontSize = 27.sp, lineHeight = 32.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                        Text("Freshly prepared favourites, made to order", fontSize = 12.sp, color = Muted)
                    }
                    Surface(color = Color(0xFFFFF0E5), shape = RoundedCornerShape(15.dp)) {
                        Column(Modifier.padding(horizontal = 13.dp, vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("LB", color = Gold, fontWeight = FontWeight.Black, fontSize = 21.sp)
                            Text("FOOD", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 8.sp, letterSpacing = 1.sp)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = search, onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search for dishes", color = Muted, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = "Search dishes", tint = Ink) },
                    trailingIcon = { if (search.isNotEmpty()) IconButton(onClick = { search = "" }) { Icon(Icons.Default.Remove, contentDescription = "Clear search") } },
                    singleLine = true, shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFE2E2E2), focusedBorderColor = Ink
                    )
                )
            }
        }
        if (!filterMenu && search.isBlank()) {
            item {
                Column(Modifier.fillMaxWidth().padding(bottom = 18.dp)) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(Triple("Veg", Icons.Default.RiceBowl, "VEG"), Triple("Non-veg", Icons.Default.LocalFireDepartment, "NON-VEG"), Triple("Roti & naan", Icons.Default.BakeryDining, "ROTI")).forEach { (title, categoryIcon, key) ->
                            Column(Modifier.weight(1f).clickable { onCategory(key) }, horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(color = CardColor, shape = CircleShape, modifier = Modifier.size(74.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Icon(categoryIcon, contentDescription=title, tint=if(key=="VEG") Color(0xFF27834A) else if(key=="ROTI") Gold else Color(0xFFB33A2B), modifier=Modifier.size(34.dp)) }
                                }
                                Spacer(Modifier.height(7.dp))
                                Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Ink, maxLines = 1)
                            }
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    Surface(color = Color(0xFF202124), shape = RoundedCornerShape(0.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(horizontal = 20.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("MADE FRESH, SERVED HOT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gold, letterSpacing = 1.sp)
                                Spacer(Modifier.height(5.dp))
                                Text("Your everyday food cravings, sorted.", fontSize = 21.sp, lineHeight = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                Spacer(Modifier.height(10.dp))
                                Text("Rice • Noodles • Chicken • Roti", fontSize = 11.sp, color = Color(0xFFE1E1E1))
                            }
                            AsyncImage(model=foodPhoto(menu[4]),contentDescription="Chicken dish",contentScale=ContentScale.Crop,modifier=Modifier.size(94.dp).clip(RoundedCornerShape(14.dp)))
                        }
                    }
                }
            }
        }
        item {
            Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
                Text(if (search.isNotBlank()) "Search results" else if (filterMenu) "Full menu" else "Popular dishes", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                Spacer(Modifier.height(3.dp))
                Text(if (search.isNotBlank()) "${filtered.size} dishes found" else "Laddu Bhai Food Point · ${filtered.size} items", fontSize = 12.sp, color = Muted)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
                    categories.forEach { c ->
                        FilterChip(
                            selected = category == c, onClick = { onCategory(c) },
                            label = { Text(if (c == "ALL") "All" else c.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Ink, selectedLabelColor = Color.White)
                        )
                    }
                }
            }
        }
        if (filtered.isEmpty()) {
            item {
                Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No dishes found", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Ink)
                    Text("Try a different search or filter.", color = Muted, fontSize = 13.sp)
                    TextButton(onClick = { search = ""; onCategory("ALL") }) { Text("Clear filters") }
                }
            }
        } else {
            listOf("NON-VEG", "VEG", "ROTI").filter { group -> filtered.any { it.category == group } }.forEach { group ->
                item(key = "category_header_$group") {
                    Row(Modifier.fillMaxWidth().background(Color(0xFFF6F5F0)).padding(horizontal = 18.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(when(group) { "NON-VEG" -> "NON-VEGETARIAN"; "VEG" -> "VEGETARIAN"; else -> "ROTI & NAAN" }, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Ink, letterSpacing = 1.sp)
                        Spacer(Modifier.weight(1f))
                        Text("${filtered.count { it.category == group }} items", fontSize = 11.sp, color = Muted)
                    }
                }
                items(filtered.filter { it.category == group }, key = { it.name }) { food ->
                    FoodCard(food, cart, onAdd, onRemove, onFoodClick)
                }
            }
        }
        item {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Divider(color = Color(0xFFEAEAEA))
                Spacer(Modifier.height(14.dp))
                Text("Laddu Bhai Food Point", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                Text("Freshly prepared food · Party orders welcome", color = Muted, fontSize = 12.sp)
            }
        }
    }
}

@Composable private fun FoodCard(f:Food,cart:Map<String,Line>,onAdd:(Food,String,Int)->Unit,onRemove:(Food,String)->Unit,onClick:(Food)->Unit){
    val accent = if (f.category == "VEG") Color(0xFF27834A) else if (f.category == "ROTI") Color(0xFF9A6414) else Color(0xFFB33A2B)
    val singleQty = cart[itemKey(f, "Single")]?.qty ?: 0
    val plateQty = cart[itemKey(f, "Plate")]?.qty ?: 0
    Row(
        Modifier.fillMaxWidth().background(Color.White).clickable { onClick(f) }.padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f).padding(end = 12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(10.dp).background(Color.White, RoundedCornerShape(2.dp)).padding(1.dp).border(1.dp, accent, RoundedCornerShape(2.dp)))
                Spacer(Modifier.width(5.dp))
                Text(f.category, fontSize = 9.sp, color = accent, fontWeight = FontWeight.ExtraBold, letterSpacing = .6.sp)
            }
            Text(f.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 20.sp)
            Text(if (f.plate != null) "₹${f.single}  ·  Single" else "₹${f.single} per serving", fontSize = 13.sp, color = Ink, fontWeight = FontWeight.SemiBold)
            Text("Freshly prepared", fontSize = 11.sp, color = Muted)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (singleQty == 0) {
                    OutlinedButton(onClick = { onClick(f) }, contentPadding = PaddingValues(horizontal = 13.dp, vertical = 0.dp), modifier = Modifier.height(34.dp), shape = RoundedCornerShape(8.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF168044))) {
                        Text("ADD +", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF168044))
                    }
                } else {
                    Surface(color = Color(0xFF168044), shape = RoundedCornerShape(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(34.dp).padding(horizontal = 3.dp)) {
                            IconButton(onClick = { onRemove(f, "Single") }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Remove, null, tint = Color.White, modifier = Modifier.size(15.dp)) }
                            Text("$singleQty", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            IconButton(onClick = { onAdd(f, "Single", f.single) }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(15.dp)) }
                        }
                    }
                }
                if (f.plate != null) {
                    Text("Plate ₹${f.plate}", fontSize = 11.sp, color = Muted)
                }
            }
        }
        Box {
            AsyncImage(model=foodPhoto(f),contentDescription=f.name,contentScale=ContentScale.Crop,modifier=Modifier.size(116.dp,112.dp).clip(RoundedCornerShape(12.dp)))
            if (f.plate != null) {
                Surface(color = Color.White, shape = RoundedCornerShape(7.dp), modifier = Modifier.align(Alignment.BottomCenter).offset(y = 7.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE6E6E6))) {
                    Text(if (plateQty > 0) "PLATE · $plateQty" else "PLATE ₹${f.plate}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 9.sp, color = Ink, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
    Divider(Modifier.padding(horizontal = 18.dp), color = Color(0xFFECECEC))
}
@Composable private fun PriceOption(f:Food,v:String,p:Int,cart:Map<String,Line>,onAdd:(Food,String,Int)->Unit,onRemove:(Food,String)->Unit,modifier:Modifier){
 val qty=cart[itemKey(f,v)]?.qty?:0
 Surface(modifier=modifier,shape=RoundedCornerShape(15.dp),color=if(qty>0)Color(0xFFFFF2C7) else Color(0xFFF8F7F2),border=androidx.compose.foundation.BorderStroke(1.dp,if(qty>0)Gold else Color(0xFFECE9DF))){
  Column(Modifier.padding(horizontal=11.dp,vertical=10.dp),verticalArrangement=Arrangement.spacedBy(7.dp)){
   Text(v.uppercase(),fontSize=9.sp,color=Muted,fontWeight=FontWeight.Bold,letterSpacing=.7.sp)
   Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){
    Text("₹$p",fontSize=18.sp,fontWeight=FontWeight.ExtraBold,color=Ink)
    if(qty==0)IconButton(onClick={onAdd(f,v,p)},modifier=Modifier.size(32.dp).background(Gold,RoundedCornerShape(10.dp))){Icon(Icons.Default.Add,contentDescription="Add ${f.name} ${v}",tint=Ink,modifier=Modifier.size(19.dp))}
    else Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(2.dp)){
     IconButton(onClick={onRemove(f,v)},modifier=Modifier.size(27.dp)){Icon(Icons.Default.Remove,contentDescription="Remove one ${f.name}",modifier=Modifier.size(16.dp))}
     Text("$qty",fontWeight=FontWeight.ExtraBold,fontSize=13.sp,color=Ink)
     IconButton(onClick={onAdd(f,v,p)},modifier=Modifier.size(27.dp)){Icon(Icons.Default.Add,contentDescription="Add one ${f.name}",modifier=Modifier.size(16.dp))}
    }
   }
  }
 }
}
private fun changeQty(cart:Map<String,Line>,set:(Map<String,Line>)->Unit,line:Line,delta:Int){
 val k=itemKey(line.food,line.variant);val old=cart[k];val qty=(old?.qty?:line.qty)+delta
 set(if(qty<=0)cart-k else cart+(k to line.copy(qty=qty)))
}
private fun setLineQty(cart:Map<String,Line>,set:(Map<String,Line>)->Unit,line:Line,qty:Int){
 val k=itemKey(line.food,line.variant)
 set(if(qty<=0)cart-k else cart+(k to line.copy(qty=qty)))
}

@Composable private fun FoodDetailPage(f:Food,cart:Map<String,Line>,onAdd:(Food,String,Int)->Unit,onAddBatch:(Food,String,Int,Int)->Unit,onBack:()->Unit,onRemove:(Food,String)->Unit){
 var variant by remember(f.name){mutableStateOf("Single")}
 var qty by remember(f.name){mutableIntStateOf(1)}
 val price=if(variant=="Plate") (f.plate?:f.single) else f.single
 Column(Modifier.fillMaxSize().background(Cream).padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
  
  Surface(color=Color(0xFF123D2B),shape=RoundedCornerShape(28.dp),modifier=Modifier.fillMaxWidth()){
   Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.Start){
    AsyncImage(model=foodPhoto(f),contentDescription=f.name,contentScale=ContentScale.Crop,modifier=Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(topStart=28.dp,topEnd=28.dp)))
    Column(Modifier.padding(22.dp)){
    Text(f.name,fontSize=27.sp,fontWeight=FontWeight.ExtraBold,color=Color.White)
    Text(when(f.category){"VEG"->"VEGETARIAN";"ROTI"->"FRESHLY MADE";else->"NON-VEGETARIAN"},fontSize=10.sp,color=Gold,fontWeight=FontWeight.Bold,letterSpacing=1.5.sp)
    Spacer(Modifier.height(8.dp));Text("Freshly prepared at Laddu Bhai Food Point.",fontSize=13.sp,color=Color(0xFFE0EAE3))
    }
   }
  }
  Text("Choose your serving",fontSize=20.sp,fontWeight=FontWeight.ExtraBold,color=Ink)
  Row(horizontalArrangement=Arrangement.spacedBy(10.dp),modifier=Modifier.fillMaxWidth()){
   listOf("Single" to f.single).plus(if(f.plate!=null) listOf("Plate" to f.plate!!) else emptyList()).forEach{(v,p)->
    Surface(color=if(variant==v)Color(0xFFFFF0BD) else Color.White,shape=RoundedCornerShape(16.dp),border=androidx.compose.foundation.BorderStroke(1.dp,if(variant==v)Gold else Color.LightGray),modifier=Modifier.weight(1f).clickable{variant=v}){
     Column(Modifier.padding(16.dp)){Text(v,fontWeight=FontWeight.Bold,color=Ink);Text("₹$p",fontSize=22.sp,fontWeight=FontWeight.ExtraBold,color=Ink)}
    }
   }
  }
  Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){Text("Quantity",fontSize=18.sp,fontWeight=FontWeight.Bold,color=Ink);Row(verticalAlignment=Alignment.CenterVertically){IconButton(onClick={if(qty>1)qty--}){Icon(Icons.Default.Remove,null)};Text("$qty",fontWeight=FontWeight.Bold,fontSize=18.sp);IconButton(onClick={qty++}){Icon(Icons.Default.Add,null)}}}
  Spacer(Modifier.weight(1f))
  Button(onClick={repeat(qty){onAdd(f,variant,price)};onBack()},modifier=Modifier.fillMaxWidth().height(54.dp),shape=RoundedCornerShape(16.dp),colors=ButtonDefaults.buttonColors(containerColor=Ink)){Text("Add to cart  ·  ₹${price*qty}",color=Gold,fontWeight=FontWeight.Bold,fontSize=16.sp)}
 }
}

@Composable
private fun OrderDetailPage(order: OrderRecord, onBack: () -> Unit) {
 val orderItems = remember(order.itemsJson) { runCatching { val a=JSONArray(order.itemsJson); (0 until a.length()).map { i -> a.getJSONObject(i) } }.getOrDefault(emptyList()) }
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
   Row(verticalAlignment=Alignment.CenterVertically) { Column { Text("ORDER DETAILS",fontSize=10.sp,color=Color(0xFF18794E),fontWeight=FontWeight.Bold,letterSpacing=1.2.sp); Text(order.id,fontSize=18.sp,fontWeight=FontWeight.ExtraBold,color=Ink) } }
   Surface(color=Color(0xFFEAF5EE),shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) { Text(order.status,fontSize=18.sp,fontWeight=FontWeight.ExtraBold,color=Color(0xFF18794E)); Text("Status saved with this order · live restaurant updates are not connected",fontSize=12.sp,color=Muted); Text("Order type: " + order.fulfillment + " · Payment: " + order.paymentMethod,fontWeight=FontWeight.SemiBold,color=Ink); Text("Order ID: " + order.id,fontSize=12.sp,color=Muted) } }
   Surface(color=Color.White,shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) { Text("Personal information",fontSize=18.sp,fontWeight=FontWeight.ExtraBold,color=Ink); Text("Customer name",fontSize=11.sp,color=Muted); Text(order.customerName.ifBlank{"Not provided"},fontWeight=FontWeight.SemiBold,color=Ink); Text("Mobile number",fontSize=11.sp,color=Muted); Text(order.phone.ifBlank{"Not provided"},fontWeight=FontWeight.SemiBold,color=Ink) } }
   Surface(color=Color.White,shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) { Text("Items ordered",fontSize=18.sp,fontWeight=FontWeight.ExtraBold,color=Ink); if(orderItems.isEmpty()) Text("Item details are unavailable for this older order.",fontSize=13.sp,color=Muted); orderItems.forEach { item -> Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(item.optString("name","Item"),fontWeight=FontWeight.SemiBold,color=Ink); Text("Qty " + item.optInt("qty",1) + " · " + item.optString("variant",""),fontSize=12.sp,color=Muted) }; Text("₹" + item.optInt("lineTotal",item.optInt("price")*item.optInt("qty",1)),fontWeight=FontWeight.Bold,color=Ink) } }; Text("Food preferences: " + order.preferences,fontSize=12.sp,color=Muted) } }
   Surface(color=Color.White,shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) { Text(if(order.fulfillment=="Delivery") "Delivery information" else "Pickup information",fontSize=18.sp,fontWeight=FontWeight.ExtraBold,color=Ink); if(order.fulfillment=="Delivery") { Text("Delivery address",fontSize=11.sp,color=Muted); Text(order.address.ifBlank{"Not provided"},fontWeight=FontWeight.Medium,color=Ink); Text("Driving distance: " + (if(order.distanceKm>0) "%.2f".format(Locale.US,order.distanceKm)+" km" else "Unavailable"),fontSize=13.sp,color=Muted); Text("Delivery fee: ₹" + order.deliveryFee,fontWeight=FontWeight.SemiBold,color=Ink) } else Text("Customer collects the order at the restaurant.",fontSize=13.sp,color=Muted) } }
   Surface(color=Color.White,shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) { Text("Bill details",fontSize=18.sp,fontWeight=FontWeight.ExtraBold,color=Ink); Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) { Text("Food subtotal",color=Muted); Text("₹" + order.itemTotal,fontWeight=FontWeight.SemiBold,color=Ink) }; Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) { Text("Packaging fee",color=Muted); Text("₹" + order.packagingFee,fontWeight=FontWeight.SemiBold,color=Ink) }; Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) { Text("Delivery fee",color=Muted); Text("₹" + order.deliveryFee,fontWeight=FontWeight.SemiBold,color=Ink) }; Divider(color=Color(0xFFE8E7E0)); Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) { Text("Order total",fontSize=17.sp,fontWeight=FontWeight.Bold,color=Ink); Text("₹" + order.total,fontSize=22.sp,fontWeight=FontWeight.ExtraBold,color=Color(0xFF18794E)) } } }
 }
}