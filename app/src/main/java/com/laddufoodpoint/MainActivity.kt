package com.laddufoodpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink=Color(0xFF191916)
private val Gold=Color(0xFFFFC928)
private val Cream=Color(0xFFFFFBF1)
private val Muted=Color(0xFF77766F)
private data class Food(val name:String,val category:String,val single:Int,val plate:Int?=null)
private data class Line(val food:Food,val variant:String,val price:Int,val qty:Int)
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

class MainActivity:ComponentActivity(){
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{LBFPApp()}}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun LBFPApp(){
 var tab by remember{mutableStateOf("Home")}
 var category by remember{mutableStateOf("ALL")}
 var cart by remember{mutableStateOf(mapOf<String,Line>())}
 var cartOpen by remember{mutableStateOf(false)}
 var checkout by remember{mutableStateOf(false)}
 var name by remember{mutableStateOf("")}
 var phone by remember{mutableStateOf("")}
 var address by remember{mutableStateOf("")}
 var orderHistory by remember{mutableStateOf(listOf<Pair<Int,String>>())}
 var lastOrder by remember{mutableStateOf(false)}
 val count=cart.values.sumOf{it.qty}
 val total=cart.values.sumOf{it.qty*it.price}
 MaterialTheme(colorScheme=lightColorScheme(primary=Ink,secondary=Gold,background=Cream,surface=Color.White)){
 Scaffold(containerColor=Cream,topBar={
 TopAppBar(title={Column{
 Text("LADDU BHAI",fontSize=20.sp,fontWeight=FontWeight.Black,color=Ink,letterSpacing=1.sp)
 Text("FOOD POINT  ·  GOOD FOOD, HAPPY MOOD",fontSize=9.sp,color=Muted,letterSpacing=1.sp)
 }},actions={IconButton(onClick={cartOpen=true}){BadgedBox(badge={if(count>0)Badge{Text("$"+"count")}}){Icon(Icons.Default.ShoppingBag,contentDescription="Cart",tint=Ink)}}},colors=TopAppBarDefaults.topAppBarColors(containerColor=Cream))
 },bottomBar={
 if(!cartOpen&&!checkout&&!lastOrder) NavigationBar(containerColor=Color.White){
 NavigationBarItem(selected=tab=="Home",onClick={tab="Home"},icon={Icon(Icons.Default.Home,null)},label={Text("Home")})
 NavigationBarItem(selected=tab=="Menu",onClick={tab="Menu"},icon={Icon(Icons.Default.MenuBook,null)},label={Text("Menu")})
 NavigationBarItem(selected=tab=="Orders",onClick={tab="Orders"},icon={Icon(Icons.Default.ReceiptLong,null)},label={Text("Orders")})
 NavigationBarItem(selected=tab=="Profile",onClick={tab="Profile"},icon={Icon(Icons.Default.Person,null)},label={Text("Profile")})
 }
 }){padding->
 when{
 lastOrder->Column(Modifier.padding(padding).fillMaxSize().padding(26.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
 Box(Modifier.size(78.dp).background(Gold,CircleShape),contentAlignment=Alignment.Center){Text("✓",fontSize=42.sp,fontWeight=FontWeight.Black)}
 Spacer(Modifier.height(18.dp));Text("Thanks, $name!",fontSize=27.sp,fontWeight=FontWeight.ExtraBold,color=Ink)
 Text("Order summary saved on this device",color=Muted)
 Spacer(Modifier.height(12.dp));Text("Total · ₹$total",fontSize=22.sp,fontWeight=FontWeight.Bold)
 Text("Demo only: this order has not been sent to the restaurant.",fontSize=12.sp,color=Muted)
 Spacer(Modifier.height(18.dp));Button(onClick={cart=emptyMap();lastOrder=false;tab="Home"},colors=ButtonDefaults.buttonColors(containerColor=Ink)){Text("Continue exploring",color=Gold)}
 }
 checkout->Column(Modifier.padding(padding).fillMaxSize().padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 TextButton(onClick={checkout=false;cartOpen=true}){Text("← Back to cart")}
 Text("Almost there!",fontSize=28.sp,fontWeight=FontWeight.ExtraBold,color=Ink)
 Text("Where should we bring your favourites?",color=Muted)
 OutlinedTextField(name,{name=it},label={Text("Full name")},singleLine=true,modifier=Modifier.fillMaxWidth())
 OutlinedTextField(phone,{phone=it.filter{c->c.isDigit()}},label={Text("Mobile number")},singleLine=true,modifier=Modifier.fillMaxWidth())
 OutlinedTextField(address,{address=it},label={Text("Delivery address")},modifier=Modifier.fillMaxWidth(),minLines=3)
 Spacer(Modifier.weight(1f))
 Surface(color=Color.White,shape=RoundedCornerShape(18.dp)){Column(Modifier.padding(16.dp)){Text("Order total",color=Muted);Text("₹$total",fontSize=26.sp,fontWeight=FontWeight.ExtraBold);Text("Restaurant confirms delivery and payment separately.",fontSize=12.sp,color=Muted);Spacer(Modifier.height(10.dp));Button(onClick={orderHistory=listOf(total to "Order placed · confirmation pending")+orderHistory;checkout=false;cartOpen=false;lastOrder=true},enabled=name.isNotBlank()&&phone.length>=10&&address.isNotBlank(),modifier=Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Ink)){Text("Place order",color=Gold,fontWeight=FontWeight.Bold)}}}
 }
 cartOpen->Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)){
 Row(verticalAlignment=Alignment.CenterVertically){TextButton(onClick={cartOpen=false}){Text("← Back")};Text("Your bag",fontSize=26.sp,fontWeight=FontWeight.ExtraBold,color=Ink)}
 if(cart.isEmpty())Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Icon(Icons.Default.ShoppingBag,null,tint=Gold,modifier=Modifier.size(46.dp));Text("Your bag is waiting",fontSize=21.sp,fontWeight=FontWeight.Bold);Text("Add a favourite from our menu",color=Muted);Button(onClick={cartOpen=false;tab="Menu"}){Text("Explore menu")}}}
 else{
 LazyColumn(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(9.dp)){items(cart.values.toList(),key={itemKey(it.food,it.variant)}){line->Surface(color=Color.White,shape=RoundedCornerShape(16.dp)){Row(Modifier.fillMaxWidth().padding(13.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(line.food.name,fontWeight=FontWeight.Bold);Text("${line.variant} · ₹${line.price} each",fontSize=12.sp,color=Muted);Text("₹${line.price*line.qty}",fontWeight=FontWeight.Bold)};IconButton(onClick={changeQty(cart,{cart=it},line,-1)}){Icon(Icons.Default.Remove,null)};Text("${line.qty}",fontWeight=FontWeight.Bold);IconButton(onClick={changeQty(cart,{cart=it},line,1)}){Icon(Icons.Default.Add,null)}}}}}
 Surface(color=Color.White,shape=RoundedCornerShape(18.dp)){Column(Modifier.padding(16.dp)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("Total",fontSize=16.sp);Text("₹$total",fontSize=24.sp,fontWeight=FontWeight.ExtraBold)};Spacer(Modifier.height(10.dp));Button(onClick={cartOpen=false;checkout=true},modifier=Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Ink)){Text("Continue to checkout",color=Gold)}}}
 }
 }
 tab=="Orders"->Column(Modifier.padding(padding).fillMaxSize().padding(20.dp)){
 Text("Your orders",fontSize=28.sp,fontWeight=FontWeight.ExtraBold,color=Ink);Text("Your recent activity",color=Muted)
 Spacer(Modifier.height(16.dp))
 if(orderHistory.isEmpty())Surface(color=Color.White,shape=RoundedCornerShape(20.dp)){Column(Modifier.fillMaxWidth().padding(22.dp)){Icon(Icons.Default.ReceiptLong,null,tint=Gold,modifier=Modifier.size(36.dp));Spacer(Modifier.height(8.dp));Text("No orders yet",fontSize=20.sp,fontWeight=FontWeight.Bold);Text("Your next favourite meal starts here.",color=Muted);Button(onClick={tab="Menu"}){Text("Browse menu")}}}
 else LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp)){items(orderHistory){o->Surface(color=Color.White,shape=RoundedCornerShape(16.dp)){Column(Modifier.padding(16.dp)){Text(o.second,fontWeight=FontWeight.Bold);Text("Laddu Bhai Food Point",color=Muted);Text("₹${o.first}",fontWeight=FontWeight.ExtraBold,fontSize=20.sp);Text("Saved locally · not transmitted to restaurant",fontSize=11.sp,color=Muted)}}}}
 }
 tab=="Profile"->Column(Modifier.padding(padding).fillMaxSize().padding(20.dp)){
 Surface(color=Ink,shape=RoundedCornerShape(24.dp)){Column(Modifier.fillMaxWidth().padding(22.dp)){Text("LBFP",fontSize=12.sp,color=Gold,fontWeight=FontWeight.Bold,letterSpacing=2.sp);Text("Good Food, Happy Mood",fontSize=24.sp,color=Color.White,fontWeight=FontWeight.ExtraBold);Text("Laddu Bhai Food Point",color=Color.LightGray)}}
 Spacer(Modifier.height(16.dp));Text("Your details",fontSize=21.sp,fontWeight=FontWeight.Bold)
 OutlinedTextField(name,{name=it},label={Text("Name")},modifier=Modifier.fillMaxWidth(),singleLine=true)
 OutlinedTextField(phone,{phone=it.filter{c->c.isDigit()}},label={Text("Mobile number")},modifier=Modifier.fillMaxWidth(),singleLine=true)
 OutlinedTextField(address,{address=it},label={Text("Delivery address")},modifier=Modifier.fillMaxWidth(),minLines=2)
 Text("Party orders are welcome. Contact the restaurant to arrange bulk orders.",color=Muted,fontSize=13.sp)
 }
 else->HomePage(padding,tab,category,{category=it;tab="Menu"},cart,
 onAdd={f,v,price->changeQty(cart,{cart=it},Line(f,v,price,cart[itemKey(f,v)]?.qty?:0),1)},
 onRemove={f,v->val l=cart[itemKey(f,v)]?:return@HomePage;changeQty(cart,{cart=it},l,-1)},
 onMenu={tab="Menu"},
 filterMenu=tab=="Menu")
 }
 }
 }
}
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
    filterMenu: Boolean
) {
    LazyColumn(
        modifier = Modifier.padding(padding),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        if (!filterMenu) {
            item {
                Surface(color = Ink, shape = RoundedCornerShape(25.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(22.dp)) {
                        Text("MADE FRESH. SERVED WITH LOVE.", fontSize = 10.sp, color = Gold, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                        Spacer(Modifier.height(10.dp))
                        Text("Good food.\\nBetter mood.", fontSize = 32.sp, lineHeight = 37.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Spacer(Modifier.height(8.dp))
                        Text("Comfort food for every kind of craving.", fontSize = 13.sp, color = Color(0xFFE2E0D7))
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onMenu, colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
                            Text("Order your favourites  →", color = Ink, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(if (filterMenu) "Full menu" else "What are you craving?", fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                    Text(if (filterMenu) "Made fresh when you order" else "Find your next favourite", fontSize = 12.sp, color = Muted)
                }
                if (!filterMenu) TextButton(onClick = onMenu) { Text("View all →", color = Ink) }
            }
        }
        if (!filterMenu) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("VEG", "NON-VEG", "ROTI").forEach { c ->
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f).clickable { onCategory(c) }
                        ) {
                            Column(Modifier.padding(vertical = 16.dp, horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(Modifier.size(38.dp).background(if (c == "VEG") Color(0xFFE2F0D6) else if (c == "ROTI") Color(0xFFFFE9BC) else Color(0xFFF7D9D1), CircleShape), contentAlignment = Alignment.Center) {
                                    Text(if (c == "VEG") "V" else if (c == "ROTI") "R" else "N", fontWeight = FontWeight.Black, color = Ink)
                                }
                                Spacer(Modifier.height(7.dp))
                                Text(c, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Ink)
                            }
                        }
                    }
                }
            }
            item {
                Surface(color = Color(0xFFFFE7A0), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("SHARING SOMETHING SPECIAL?", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Ink, letterSpacing = 1.sp)
                            Text("Party orders welcome", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                            Text("Planning a gathering? We can help.", fontSize = 12.sp, color = Ink)
                        }
                        Text("✦", fontSize = 32.sp, color = Ink)
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("ALL", "NON-VEG", "VEG", "ROTI").forEach { c ->
                    FilterChip(
                        selected = category == c,
                        onClick = { onCategory(c) },
                        label = { Text(c, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Gold, selectedLabelColor = Ink)
                    )
                }
            }
        }
        items(menu.filter { category == "ALL" || it.category == category }, key = { it.name }) { food ->
            FoodCard(food, cart, onAdd, onRemove)
        }
        item {
            Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(17.dp)) {
                    Text("Laddu Bhai Food Point", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                    Text("Good Food, Happy Mood", color = Muted)
                    Spacer(Modifier.height(7.dp))
                    Text("We undertake all party orders.", fontSize = 12.sp, color = Muted)
                }
            }
        }
    }
}
@Composable private fun FoodCard(f:Food,cart:Map<String,Line>,onAdd:(Food,String,Int)->Unit,onRemove:(Food,String)->Unit){
 Surface(color=Color.White,shape=RoundedCornerShape(20.dp),modifier=Modifier.fillMaxWidth()){
 Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
 Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(44.dp).background(if(f.category=="VEG")Color(0xFFE2F0D6) else if(f.category=="ROTI")Color(0xFFFFE9BC) else Color(0xFFF7D9D1),RoundedCornerShape(13.dp)),contentAlignment=Alignment.Center){Text(if(f.category=="VEG")"V" else if(f.category=="ROTI")"R" else "N",fontWeight=FontWeight.Black,color=Ink)};Spacer(Modifier.width(12.dp));Column(Modifier.weight(1f)){Text(f.name,fontSize=16.sp,fontWeight=FontWeight.Bold,color=Ink,maxLines=2,overflow=TextOverflow.Ellipsis);Text(f.category,fontSize=10.sp,color=Muted,letterSpacing=1.sp)}}
 Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){PriceOption(f,"Single",f.single,cart,onAdd,onRemove,Modifier.weight(1f));if(f.plate!=null)PriceOption(f,"Plate",f.plate,cart,onAdd,onRemove,Modifier.weight(1f))}
 }
 }
}
@Composable private fun PriceOption(f:Food,v:String,p:Int,cart:Map<String,Line>,onAdd:(Food,String,Int)->Unit,onRemove:(Food,String)->Unit,modifier:Modifier){
 val qty=cart[itemKey(f,v)]?.qty?:0
 Surface(modifier=modifier,shape=RoundedCornerShape(13.dp),color=Cream){Column(Modifier.padding(10.dp)){Text(v,fontSize=11.sp,color=Muted);Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){Text("₹$p",fontSize=16.sp,fontWeight=FontWeight.ExtraBold,color=Ink);if(qty==0)IconButton(onClick={onAdd(f,v,p)},modifier=Modifier.size(30.dp).background(Gold,RoundedCornerShape(10.dp))){Icon(Icons.Default.Add,null,tint=Ink)}else Row(verticalAlignment=Alignment.CenterVertically){IconButton(onClick={onRemove(f,v)},modifier=Modifier.size(24.dp)){Icon(Icons.Default.Remove,null)};Text("$qty",fontWeight=FontWeight.Bold);IconButton(onClick={onAdd(f,v,p)},modifier=Modifier.size(24.dp)){Icon(Icons.Default.Add,null)}}}}}
}
private fun changeQty(cart:Map<String,Line>,set:(Map<String,Line>)->Unit,line:Line,delta:Int){
 val k=itemKey(line.food,line.variant);val old=cart[k];val qty=(old?.qty?:line.qty)+delta
 set(if(qty<=0)cart-k else cart+(k to line.copy(qty=qty)))
}
