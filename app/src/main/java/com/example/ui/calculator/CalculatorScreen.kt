package com.example.ui.calculator

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.calculator.components.BannerAdView
import com.example.ui.calculator.components.HistorySheet
import com.example.ui.calculator.components.IndirimHesaplaCard
import com.example.ui.calculator.components.IndirimOraniCard
import com.example.ui.calculator.components.KdvCikarCard
import com.example.ui.calculator.components.KdvEkleCard
import com.example.ui.calculator.components.QuickInfoSheet
import com.example.ui.theme.AppBackground
import com.example.ui.theme.NeutralMedium
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryPurpleLight
import com.example.ui.theme.TopBarBorder
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = remember { CalculatorViewModel() },
    systemIsDark: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = uiState.isDarkModeOverride ?: systemIsDark
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val onCopyToast: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("calculator_screen"),
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 0.dp
            ) {
                Column {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(PrimaryPurple),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Calculate,
                                        contentDescription = "App Icon",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Cep Hesap",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    )
                                    Text(
                                        text = "ALIŞVERİŞ & VERGİ ASİSTANI",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            letterSpacing = 0.8.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        },
                        actions = {
                            // Dark Mode Toggle Switch
                            IconButton(
                                onClick = {
                                    viewModel.toggleDarkMode(systemIsDark)
                                    val isNowDark = !(uiState.isDarkModeOverride ?: systemIsDark)
                                    onCopyToast(if (isNowDark) "Karanlık Mod Etkinleştirildi 🌙" else "Açık Mod Etkinleştirildi ☀️")
                                },
                                modifier = Modifier.testTag("dark_mode_toggle_btn")
                            ) {
                                Icon(
                                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Tema Değiştir",
                                    tint = if (isDark) Color(0xFFFFD54F) else PrimaryPurple
                                )
                            }

                            // Clear / Reset All Button
                            IconButton(
                                onClick = {
                                    viewModel.resetAll()
                                    onCopyToast("Tüm girdiler temizlendi")
                                },
                                modifier = Modifier.testTag("reset_all_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Temizle",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BannerAdView(modifier = Modifier.fillMaxWidth())
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    val navSelectedIconColor = if (isDark) Color.White else PrimaryPurple
                    val navSelectedTextColor = if (isDark) Color.White else PrimaryPurple
                    val navIndicatorColor = if (isDark) PrimaryPurple else PrimaryPurpleLight
                    val navUnselectedIconColor = if (isDark) Color.White else NeutralMedium
                    val navUnselectedTextColor = if (isDark) Color.White else NeutralMedium

                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                        modifier = Modifier.testTag("bottom_navigation_bar")
                    ) {
                        NavigationBarItem(
                            selected = uiState.selectedTab == 0,
                            onClick = { viewModel.selectTab(0) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.GridView,
                                    contentDescription = "Panel"
                                )
                            },
                            label = { Text("Panel", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = navSelectedIconColor,
                                selectedTextColor = navSelectedTextColor,
                                indicatorColor = navIndicatorColor,
                                unselectedIconColor = navUnselectedIconColor,
                                unselectedTextColor = navUnselectedTextColor
                            ),
                            modifier = Modifier.testTag("nav_panel_btn")
                        )

                        NavigationBarItem(
                            selected = uiState.selectedTab == 1,
                            onClick = { viewModel.selectTab(1) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "Geçmiş"
                                )
                            },
                            label = { Text("Geçmiş", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = navSelectedIconColor,
                                selectedTextColor = navSelectedTextColor,
                                indicatorColor = navIndicatorColor,
                                unselectedIconColor = navUnselectedIconColor,
                                unselectedTextColor = navUnselectedTextColor
                            ),
                            modifier = Modifier.testTag("nav_history_btn")
                        )

                        NavigationBarItem(
                            selected = uiState.selectedTab == 2,
                            onClick = { viewModel.selectTab(2) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Rehber"
                                )
                            },
                            label = { Text("Rehber", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = navSelectedIconColor,
                                selectedTextColor = navSelectedTextColor,
                                indicatorColor = navIndicatorColor,
                                unselectedIconColor = navUnselectedIconColor,
                                unselectedTextColor = navUnselectedTextColor
                            ),
                            modifier = Modifier.testTag("nav_info_btn")
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Crossfade(
            targetState = uiState.selectedTab,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { tabIndex ->
            when (tabIndex) {
                0 -> MainGridPanel(viewModel = viewModel, uiState = uiState, onCopyToast = onCopyToast)
                1 -> HistorySheet(
                    historyList = uiState.history,
                    onClearHistory = { viewModel.clearHistory() }
                )
                2 -> QuickInfoSheet(onCopyToast = onCopyToast)
            }
        }
    }
}

@Composable
private fun MainGridPanel(
    viewModel: CalculatorViewModel,
    uiState: CalculatorUiState,
    onCopyToast: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isTabletOrLandscape = configuration.screenWidthDp >= 600

    if (isTabletOrLandscape) {
        // 2x2 Grid for wide screens / tablets
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("panel_grid_wide")
        ) {
            item {
                IndirimHesaplaCard(
                    viewModel = viewModel,
                    anaFiyat = uiState.indirimHesaplaAnaFiyat,
                    yuzde = uiState.indirimHesaplaYuzde,
                    isExpanded = uiState.isIndirimHesaplaExpanded,
                    onToggleExpand = { viewModel.toggleIndirimHesaplaExpanded() },
                    onCopyToast = onCopyToast
                )
            }
            item {
                KdvEkleCard(
                    viewModel = viewModel,
                    vergisizFiyat = uiState.kdvEkleFiyat,
                    selectedOran = uiState.kdvEkleOran,
                    isOzelSelected = uiState.isKdvEkleOzelSelected,
                    ozelOranText = uiState.kdvEkleOzelOran,
                    isExpanded = uiState.isKdvEkleExpanded,
                    onToggleExpand = { viewModel.toggleKdvEkleExpanded() },
                    onCopyToast = onCopyToast
                )
            }
            item {
                KdvCikarCard(
                    viewModel = viewModel,
                    kdvliFiyat = uiState.kdvCikarFiyat,
                    selectedOran = uiState.kdvCikarOran,
                    isOzelSelected = uiState.isKdvCikarOzelSelected,
                    ozelOranText = uiState.kdvCikarOzelOran,
                    isExpanded = uiState.isKdvCikarExpanded,
                    onToggleExpand = { viewModel.toggleKdvCikarExpanded() },
                    onCopyToast = onCopyToast
                )
            }
            item {
                IndirimOraniCard(
                    viewModel = viewModel,
                    eskiFiyat = uiState.indirimOraniEskiFiyat,
                    yeniFiyat = uiState.indirimOraniYeniFiyat,
                    isExpanded = uiState.isIndirimOraniExpanded,
                    onToggleExpand = { viewModel.toggleIndirimOraniExpanded() },
                    onCopyToast = onCopyToast
                )
            }
        }
    } else {
        // Vertical Accordion Scroll Layout for portrait smartphones
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(12.dp)
                .testTag("panel_grid_vertical"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Section 1: İndirim Hesapla (Default Open)
            IndirimHesaplaCard(
                viewModel = viewModel,
                anaFiyat = uiState.indirimHesaplaAnaFiyat,
                yuzde = uiState.indirimHesaplaYuzde,
                isExpanded = uiState.isIndirimHesaplaExpanded,
                onToggleExpand = { viewModel.toggleIndirimHesaplaExpanded() },
                onCopyToast = onCopyToast
            )

            // Section 2: KDV Ekle (Default Closed)
            KdvEkleCard(
                viewModel = viewModel,
                vergisizFiyat = uiState.kdvEkleFiyat,
                selectedOran = uiState.kdvEkleOran,
                isOzelSelected = uiState.isKdvEkleOzelSelected,
                ozelOranText = uiState.kdvEkleOzelOran,
                isExpanded = uiState.isKdvEkleExpanded,
                onToggleExpand = { viewModel.toggleKdvEkleExpanded() },
                onCopyToast = onCopyToast
            )

            // Section 3: KDV Çıkar (Default Closed)
            KdvCikarCard(
                viewModel = viewModel,
                kdvliFiyat = uiState.kdvCikarFiyat,
                selectedOran = uiState.kdvCikarOran,
                isOzelSelected = uiState.isKdvCikarOzelSelected,
                ozelOranText = uiState.kdvCikarOzelOran,
                isExpanded = uiState.isKdvCikarExpanded,
                onToggleExpand = { viewModel.toggleKdvCikarExpanded() },
                onCopyToast = onCopyToast
            )

            // Section 4: % İndirim Oranı Bul (Default Closed)
            IndirimOraniCard(
                viewModel = viewModel,
                eskiFiyat = uiState.indirimOraniEskiFiyat,
                yeniFiyat = uiState.indirimOraniYeniFiyat,
                isExpanded = uiState.isIndirimOraniExpanded,
                onToggleExpand = { viewModel.toggleIndirimOraniExpanded() },
                onCopyToast = onCopyToast
            )
        }
    }
}
