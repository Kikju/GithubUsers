package sk.lighture.githubusers.ui.user

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.user_fragment.*
import kotlinx.coroutines.launch
import sk.lighture.githubusers.R

@AndroidEntryPoint
class UserFragment: BottomSheetDialogFragment() {

    private val viewModel: UserViewModel by viewModels()
    private val args: UserFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.user_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.user.observe(viewLifecycleOwner) {
            avatar.load(it.avatarUrl) {
                crossfade(true)
                placeholder(R.drawable.creeper)
            }
            name.text = it.name ?: it.login
            repositories.text = getString(R.string.repositories, it.publicRepos?.toString())

            it.htmlUrl?.let { url ->
                more.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(url)
                    }
                    if (requireActivity().packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty())
                        requireActivity().startActivity(Intent.createChooser(intent, getString(R.string.open)))
                    else
                        Toast.makeText(requireContext(), R.string.error, Toast.LENGTH_LONG).show()
                }
            }
        }



        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadUser(args.login)
        }
    }
}