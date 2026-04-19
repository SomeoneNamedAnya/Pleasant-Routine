import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.coursework.pleasantroutineui.R
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.pages.profile.buildImageCacheKey

@Composable
fun RoomUserPreview(user: User,
                    navController: NavController,
                    refreshSignedUrl: () -> Unit) {
    Row(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(Destinations.ALL_USER_INFO_PAGE.title + "/${user.id}")
            }
            .padding(start = 5.dp, end = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        println(user.photoLink)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.signedLink ?: "")
                .diskCacheKey(buildImageCacheKey(user.id, user.photoLink))
                .memoryCacheKey(buildImageCacheKey(user.id, user.photoLink))
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .networkCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .build(),
            onError = {
                refreshSignedUrl()
                Log.e(
                    "AsyncImage",
                    "Failed to load image: ${it.result.throwable}"
                )
            },
            contentDescription = "Example Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            placeholder = painterResource(id = R.drawable.no_photo),
            error = painterResource(id = R.drawable.no_photo)
        )

        Spacer(modifier = Modifier.width(15.dp))

        Text(
            text = user.about ?: "",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .weight(1f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.surface
                )
                .padding(8.dp),

            )
    }
}


