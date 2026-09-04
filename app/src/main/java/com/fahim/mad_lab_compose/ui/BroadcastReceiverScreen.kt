package com.fahim.mad_lab_compose.ui

import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fahim.mad_lab_compose.R
import com.fahim.mad_lab_compose.broadcast.AirplaneModeReceiver
import com.fahim.mad_lab_compose.broadcast.BatteryLevelReceiver
import com.fahim.mad_lab_compose.broadcast.ConnectivityReceiver
import com.fahim.mad_lab_compose.broadcast.ReceiverRegistration
import com.fahim.mad_lab_compose.ui.theme.MADLabComposeTheme

private val ScreenPadding = 32.dp
private val RowSpacing = 16.dp

/** Three switches, each of which registers a system broadcast receiver while it is on. */
@Composable
fun BroadcastReceiverScreen(modifier: Modifier = Modifier) {
    var internetEnabled by rememberSaveable { mutableStateOf(false) }
    var airplaneEnabled by rememberSaveable { mutableStateOf(false) }
    var batteryEnabled by rememberSaveable { mutableStateOf(false) }

    val connectivityReceiver = remember { ConnectivityReceiver() }
    val airplaneModeReceiver = remember { AirplaneModeReceiver() }
    val batteryLevelReceiver = remember { BatteryLevelReceiver() }

    val connectivityFilter = remember { IntentFilter(ConnectivityReceiver.ACTION_CONNECTIVITY_CHANGE) }
    val airplaneFilter = remember { IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED) }
    val batteryFilter = remember { IntentFilter(Intent.ACTION_BATTERY_CHANGED) }

    ReceiverRegistration(internetEnabled, connectivityReceiver, connectivityFilter)
    ReceiverRegistration(airplaneEnabled, airplaneModeReceiver, airplaneFilter)
    ReceiverRegistration(batteryEnabled, batteryLevelReceiver, batteryFilter)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenPadding, vertical = RowSpacing),
        verticalArrangement = Arrangement.spacedBy(RowSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.broadcast_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        BroadcastSwitchRow(
            label = stringResource(R.string.switch_internet),
            checked = internetEnabled,
            onCheckedChange = { internetEnabled = it },
        )
        BroadcastSwitchRow(
            label = stringResource(R.string.switch_airplane),
            checked = airplaneEnabled,
            onCheckedChange = { airplaneEnabled = it },
        )
        BroadcastSwitchRow(
            label = stringResource(R.string.switch_battery),
            checked = batteryEnabled,
            onCheckedChange = { batteryEnabled = it },
        )
    }
}

@Composable
private fun BroadcastSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Preview(showBackground = true)
@Composable
private fun BroadcastReceiverScreenPreview() {
    MADLabComposeTheme {
        BroadcastReceiverScreen()
    }
}
