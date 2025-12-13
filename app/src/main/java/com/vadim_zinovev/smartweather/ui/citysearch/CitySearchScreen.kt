package com.vadim_zinovev.smartweather.ui.citysearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vadim_zinovev.smartweather.domain.model.City

@Composable
fun CitySearchScreen(
    onCitySelected: (City) -> Unit,
    showAddButton: Boolean = false,
    onAddFavorite: ((City) -> Unit)? = null,
    viewModel: CitySearchViewModel = viewModel()
) {
    val state = viewModel.uiState

    val bg = Brush.verticalGradient(listOf(Color(0xFF000000), Color(0xFF121212)))
    val buttonColor = Color(0xFF1E1E1E)
    val textColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.onQueryChange(it) },
                label = { Text("Search city") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = textColor.copy(alpha = 0.8f),
                    unfocusedBorderColor = textColor.copy(alpha = 0.4f),
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedLabelColor = textColor.copy(alpha = 0.8f),
                    unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                    cursorColor = textColor
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.search() },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = textColor
                )
            ) {
                Text(text = "Search")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = textColor)
                    }
                }

                state.errorMessage != null -> {
                    Text(text = "Error: ${state.errorMessage}", color = textColor)
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(state.results) { index, city ->
                            CityListItem(
                                city = city,
                                onClick = { onCitySelected(city) },
                                showAddButton = showAddButton,
                                onAddFavorite = { onAddFavorite?.invoke(city) },
                                buttonColor = buttonColor,
                                textColor = textColor
                            )

                            if (index < state.results.lastIndex) {
                                Divider(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    color = textColor.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CityListItem(
    city: City,
    onClick: () -> Unit,
    showAddButton: Boolean,
    onAddFavorite: () -> Unit,
    buttonColor: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "📍", modifier = Modifier.padding(end = 8.dp))
            Column {
                Text(text = "${city.name}, ${city.country}", color = textColor)
            }
        }

        if (showAddButton) {
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onAddFavorite,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = textColor
                )
            ) {
                Text("Add")
            }
        }
    }
}
