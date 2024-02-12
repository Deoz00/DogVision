import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.dogvision.Home
import com.example.dogvision.List

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        // Retorna el número total de fragmentos
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        // Retorna el fragmento correspondiente a la posición
        return when (position) {
            0 -> Home()
            1 -> List()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
