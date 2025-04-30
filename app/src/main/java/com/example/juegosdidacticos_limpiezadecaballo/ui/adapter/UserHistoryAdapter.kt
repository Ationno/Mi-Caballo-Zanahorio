import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.juegosdidacticos_limpiezadecaballo.R
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import com.example.juegosdidacticos_limpiezadecaballo.data.model.GameStateEntity
import java.text.SimpleDateFormat
import java.util.Locale

class UserHistoryAdapter : ListAdapter<GameStateEntity, UserHistoryAdapter.GameStateViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameStateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game_state_row, parent, false)
        return GameStateViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameStateViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class GameStateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val errorsTextView: TextView = itemView.findViewById(R.id.errorsTextView)
        private val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
        private val difficultyTextView: TextView = itemView.findViewById(R.id.difficultyTextView)
        private val timeRatioTextView: TextView = itemView.findViewById(R.id.timeRatioTextView)

        fun bind(item: GameStateEntity) {
            val textSubDifficulty: Int = item.subDifficulty.ordinal + 1

            val dateFormat = SimpleDateFormat("dd/MM/yy", Locale("es", "AR"))
            val formattedDate = dateFormat.format(item.date)
            dateTextView.text = formattedDate
            errorsTextView.text = String.format(Locale.getDefault(), "%d", item.errors)
            scoreTextView.text = String.format(Locale.getDefault(), "%d", item.score)
            difficultyTextView.text = buildString {
                append(item.difficulty.getDisplayDifficulty())
                append("/")
                append(textSubDifficulty)
            }
            timeRatioTextView.text = String.format(Locale.getDefault(), "%s", formatTime(item.timePlayed))
        }

        private fun formatTime(time: Long): String {
            val minutes = (time / 1000) / 60
            val seconds = (time / 1000) % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<GameStateEntity>() {
        override fun areItemsTheSame(oldItem: GameStateEntity, newItem: GameStateEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GameStateEntity, newItem: GameStateEntity): Boolean {
            return oldItem == newItem
        }
    }
}