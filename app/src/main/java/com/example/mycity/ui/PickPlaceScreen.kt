package com.example.mycity.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mycity.R
import com.example.mycity.data.Place
import com.example.mycity.ui.theme.Shapes
import kotlinx.coroutines.flow.StateFlow


@Composable
fun PickAPlaceScreen(
    uiState: StateFlow<MyCityUiState>,
    modifier: Modifier = Modifier
) {
    LazyColumn() {
        items(uiState.value.currentCategory.list) {
            PlaceCard(
                place = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = dimensionResource(
                            id = R.dimen.padding_small
                        ),
                        start = dimensionResource(id = R.dimen.padding_medium),
                        end = dimensionResource(id = R.dimen.padding_medium)
                    )
            )
        }
    }

}

@Composable
fun PlaceCard(place: Place, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(
            topEnd = 20.dp, topStart = 40.dp,
            bottomEnd = 20.dp, bottomStart = 40.dp
        ),
    ) {
        Row(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = place.photo), contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(end = dimensionResource(id = R.dimen.padding_small))
                    .size(70.dp)
                    .clip(shape = Shapes.small)
            )
            Text(text = stringResource(id = place.name))
        }
    }


}

@Preview
@Composable
fun PlaceCardPreview() {
    PlaceCard(
        place = Place(
            name = R.string.kavkaz_title,
            description = R.string.kavkaz_description,
            address = R.string.kavkaz_address,
            photo = R.drawable.kavkaz
        )
    )

}