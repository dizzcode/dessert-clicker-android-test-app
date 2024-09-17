package dizzcode.com.dessertclicker

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import dizzcode.com.dessertclicker.data.Datasource
import dizzcode.com.dessertclicker.model.Dessert
import dizzcode.com.dessertclicker.ui.theme.DessertClickerTheme

/* NOTE 01
A good convention is to declare a TAG constant in your file as its value will not change.

To mark it as a compile-time constant, use const when declaring the variable.
A compile-time constant is a value that is known during compilation.
 */
private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate Called")

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            DessertClickerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                ) {
                    DessertClickerApp(desserts = Datasource.dessertList)
                }
            }
        }
    }

    //Override Methods -> press Control+O

    //app is started and onStart() is called
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart Called")
    }

    //the app becomes visible on the screen. When onResume() is called
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume Called")
    }

    /*
    When the activity returns to the foreground,
    the onCreate() method is not called again.
    The activity object was not destroyed,
    so it doesn't need to be created again.
    Instead of onCreate(), the onRestart() method is called.

    onRestart Called
    onStart Called
    onResume Called
     */
    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart Called")
    }

    //When the app goes into the background, the focus is lost after onPause()
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause Called")
    }


    //the app is no longer visible after onStop()
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop Called")
    }

    //the activity shuts down, it calls
    // onPause(), onStop(), and onDestroy(), in that order.
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy Called")
    }

    /* Note 02
    Data loss on device rotation

    Notice that when the device or emulator rotates the screen,
    the system calls all the lifecycle callbacks to shut down the activity.
    Then, as the activity is re-created,
    the system calls all the lifecycle callbacks to start the activity.

    When the device is rotated, and the activity is shut down and re-created,
    the activity re-starts with default values

     */


    /** Note 03 ***
    Lifecycle of a composable

    The UI of your app is initially built
    from running composable functions in a process called Composition.

    When the state of your app changes, a recomposition is scheduled.

    In order for Compose to track and trigger a recomposition,
    it needs to know when state has changed.
    To indicate to Compose that it should track an object's state,
    the object needs to be of type State or MutableState.
    The State type is immutable and can only be read.
    A MutableState type is mutable and allows reads and writes.

     */
}

/**
 * Determine which dessert to show.
 */
fun determineDessertToShow(
    desserts: List<Dessert>,
    dessertsSold: Int
): Dessert {
    var dessertToShow = desserts.first()
    for (dessert in desserts) {
        if (dessertsSold >= dessert.startProductionAmount) {
            dessertToShow = dessert
        } else {
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            break
        }
    }

    return dessertToShow
}

/**
 * Share desserts sold information using ACTION_SEND intent
 */
private fun shareSoldDessertsInformation(intentContext: Context, dessertsSold: Int, revenue: Int) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            intentContext.getString(R.string.share_text, dessertsSold, revenue)
        )
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)

    try {
        ContextCompat.startActivity(intentContext, shareIntent, null)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            intentContext,
            intentContext.getString(R.string.sharing_not_available),
            Toast.LENGTH_LONG
        ).show()
    }
}

@Composable
private fun DessertClickerApp(
    desserts: List<Dessert>
) {

    /** Note 04
     *
     * To create the mutable variable revenue, you declare it using mutableStateOf.
     * 0 is its initial default value.
     *
     * var revenue = mutableStateOf(0)
     *-------------------------------------------------------------------------
     * While this is enough to have Compose trigger a recomposition when the
     * revenue value changes, it is not enough to retain its updated value.
     * Each time the composable is reexecuted, it will reinitialize the
     * revenue value to its initial default value of 0.
     *
     * To instruct Compose to retain and reuse its value during recompositions,
     * you need to declare it with the remember API.
     *
     * var revenue by remember { mutableStateOf(0) }
     *
     * If the value of revenue changes,
     * Compose schedules all composable functions that read this value for recomposition.
     *
     *-------------------------------------------------------------------------
     */

    /** Note 05
     * rememberSaveable
     *
     * Use rememberSaveable to save values across configuration changes
     *
     * You use the rememberSaveable function to save values that you need if Android OS destroys and recreates the activity.
     *
     * To save values during recompositions, you need to use remember.
     * Use rememberSaveable to save values during recompositions AND configuration changes.
     *
     *-------------------------------------------------------------------------
     * var revenue by remember { mutableStateOf(0) }
     * to ->
     *var revenue by rememberSaveable { mutableStateOf(0) }
     *
     * -------------------------------------------------------------------------
     *
     * var currentDessertImageId by remember {
     *     mutableStateOf(desserts[currentDessertIndex].imageId)
     * }
     * to ->
     * var currentDessertImageId by rememberSaveable {
     *     mutableStateOf(desserts[currentDessertIndex].imageId)
     * }
     *
     *-------------------------------------------------------------------------
     */


    //var revenue by remember { mutableStateOf(0) }
    var revenue by rememberSaveable { mutableStateOf(0) }

    var dessertsSold by remember { mutableStateOf(0) }

    val currentDessertIndex by remember { mutableStateOf(0) }

    var currentDessertPrice by remember {
        mutableStateOf(desserts[currentDessertIndex].price)
    }
//    var currentDessertImageId by remember {
//        mutableStateOf(desserts[currentDessertIndex].imageId)
//    }
    var currentDessertImageId by rememberSaveable {
        mutableStateOf(desserts[currentDessertIndex].imageId)
    }

    Scaffold(
        topBar = {
            val intentContext = LocalContext.current
            val layoutDirection = LocalLayoutDirection.current
            DessertClickerAppBar(
                onShareButtonClicked = {
                    shareSoldDessertsInformation(
                        intentContext = intentContext,
                        dessertsSold = dessertsSold,
                        revenue = revenue
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = WindowInsets.safeDrawing
                            .asPaddingValues()
                            .calculateStartPadding(layoutDirection),
                        end = WindowInsets.safeDrawing
                            .asPaddingValues()
                            .calculateEndPadding(layoutDirection),
                    )
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    ) { contentPadding ->
        DessertClickerScreen(
            revenue = revenue,
            dessertsSold = dessertsSold,
            dessertImageId = currentDessertImageId,
            onDessertClicked = {

                // Update the revenue
                revenue += currentDessertPrice
                dessertsSold++

                // Show the next dessert
                val dessertToShow = determineDessertToShow(desserts, dessertsSold)
                currentDessertImageId = dessertToShow.imageId
                currentDessertPrice = dessertToShow.price
            },
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@Composable
private fun DessertClickerAppBar(
    onShareButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_medium)),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge,
        )
        IconButton(
            onClick = onShareButtonClicked,
            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_medium)),
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = stringResource(R.string.share),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun DessertClickerScreen(
    revenue: Int,
    dessertsSold: Int,
    @DrawableRes dessertImageId: Int,
    onDessertClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(R.drawable.bakery_back),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                Image(
                    painter = painterResource(dessertImageId),
                    contentDescription = null,
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.image_size))
                        .height(dimensionResource(R.dimen.image_size))
                        .align(Alignment.Center)
                        .clickable { onDessertClicked() },
                    contentScale = ContentScale.Crop,
                )
            }
            TransactionInfo(
                revenue = revenue,
                dessertsSold = dessertsSold,
                modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
            )
        }
    }
}

@Composable
private fun TransactionInfo(
    revenue: Int,
    dessertsSold: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        DessertsSoldInfo(
            dessertsSold = dessertsSold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
        )
        RevenueInfo(
            revenue = revenue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
        )
    }
}

@Composable
private fun RevenueInfo(revenue: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.total_revenue),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = "$${revenue}",
            textAlign = TextAlign.Right,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun DessertsSoldInfo(dessertsSold: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.dessert_sold),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = dessertsSold.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Preview
@Composable
fun MyDessertClickerAppPreview() {
    DessertClickerTheme {
        DessertClickerApp(listOf(Dessert(R.drawable.cupcake, 5, 0)))
    }
}
