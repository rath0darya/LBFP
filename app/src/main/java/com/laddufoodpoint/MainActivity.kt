package com.laddufoodpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ink=Color(0xFF191916)
private val gold=Color(0xFFFFC928)
private val menu=listOf("Egg Fried Rice · ₹70 / ₹130","Egg Bhurji · ₹60 / ₹120","Chicken Fried Rice · ₹100 / ₹170","Egg Noodles · ₹70 / ₹130","Chicken 65 · ₹120 / ₹220","Chicken Manchurian · ₹120 / ₹220","Chicken Drumstick · ₹120 / ₹240","Chicken Chilli · ₹130 / ₹230","Schezwan Chicken · ₹70 / ₹120","Chicken Leg Piece · ₹100","Veg Fried Rice · ₹60 / ₹110","Veg Noodles · ₹60 / ₹110","Veg Manchurian · ₹80 / ₹140","Veg Manchurian Noodles · ₹80 / ₹140","Jeera Fried Rice · ₹60 / ₹110","Rumali Roti · ₹12","Tandoori Roti · ₹13","Butter Naan · ₹30")
class MainActivity:ComponentActivity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent { Screen() }}}
@Composable private fun Screen(){var filter by remember { mutableStateOf("ALL") }; var cart by remember { mutableStateOf(0) }; var total by remember { mutableStateOf(0) }; var checkout by remember { mutableStateOf(false) }; MaterialTheme(colorScheme=lightColorScheme(primary=ink,secondary=gold)){Scaffold(containerColor=Color(0xFFFFFBF1),topBar={TopAppBar(title={Column{Text("LADDU BHAI",fontWeight=FontWeight.Black,fontSize=21.sp);Text("FOOD POINT · GOOD FOOD, HAPPY MOOD",fontSize=9.sp)}})},bottomBar={if(cart>0) Row(Modifier.fillMaxWidth().background(Color.White).padding(16.dp),horizontalArrangement=Arrangement.SpaceBetween){Text("$cart items · ₹$total",fontWeight=FontWeight.Bold);Button(onClick={checkout=true}){Text("Checkout")}}}){p->if(checkout){Column(Modifier.padding(p).padding(20.dp)){Text("Order summary",fontSize=25.sp,fontWeight=FontWeight.Bold);Text("Cart: $cart items · ₹$total");Text("Demo checkout only; order is not sent to the restaurant.");Button(onClick={checkout=false}){Text("Back to menu")}}}else LazyColumn(Modifier.padding(p),contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){item{Column(Modifier.fillMaxWidth().background(ink,RoundedCornerShape(24.dp)).padding(22.dp)){Text("GOOD FOOD, HAPPY MOOD",color=gold,fontWeight=FontWeight.Bold);Spacer(Modifier.height(8.dp));Text("Your cravings. Our kitchen.",color=Color.White,fontSize=28.sp,fontWeight=FontWeight.ExtraBold);Text("We undertake all party orders",color=gold)}};item{Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){listOf("ALL","VEG","NON-VEG","ROTI").forEach{c->FilterChip(selected=filter==c,onClick={filter=c},label={Text(c)})}}};items(menu.filter { filter=="ALL" || (filter=="VEG"&&it.startsWith("Veg")||filter=="NON-VEG"&& !it.startsWith("Veg")&&!it.contains("Roti")&&!it.contains("Naan")||filter=="ROTI"&&(it.contains("Roti")||it.contains("Naan"))) }){item->Surface(color=Color.White,shape=RoundedCornerShape(16.dp)){Row(Modifier.fillMaxWidth().padding(14.dp),horizontalArrangement=Arrangement.SpaceBetween){Text(item,Modifier.weight(1f),fontWeight=FontWeight.SemiBold);Button(onClick={cart++;total+=item.substringAfter("₹").substringBefore(" ").toIntOrNull()?:0}){Text("+ Add")}}}}}}}}
