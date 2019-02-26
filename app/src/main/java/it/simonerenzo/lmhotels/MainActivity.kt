package it.simonerenzo.lmhotels

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.transaction
import com.google.android.material.navigation.NavigationView
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.materialize.MaterializeBuilder
import it.simonerenzo.lmhotels.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)

        MaterializeBuilder().withActivity(this).build()

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, mainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(R.id.nav_home)

        supportActionBar?.title = resources.getString(R.string.title_fragment_home)

        supportFragmentManager.transaction {
            replace(R.id.mainFrame, HomeFragment())
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.transaction {
                    replace(R.id.mainFrame, HomeFragment())
                }
                supportActionBar?.title = resources.getString(R.string.title_fragment_home)
            }
            R.id.nav_about -> {
                val fragment = LibsBuilder()
                    .withAboutAppName(resources.getString(R.string.app_name))
                    .withAboutIconShown(true)
                    .withAboutVersionShown(true)
                    .withAboutDescription(resources.getString(R.string.app_descr))
                    .supportFragment()

                supportFragmentManager.transaction {
                    replace(R.id.mainFrame, fragment)
                }

                supportActionBar?.title = resources.getString(R.string.title_fragment_about)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
