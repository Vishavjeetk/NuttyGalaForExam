package com.example.nuttygala

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nuttygala.models.CartModel
import com.example.nuttygala.models.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import ru.nikartm.support.ImageBadgeView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    FragmentToActivity {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var drawerLayout: DrawerLayout

    var account: GoogleSignInAccount? = null
    var badgeValue = 0
    private var actionView: View? = null

    fun getEmail(): String {
        return account?.email.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        supportActionBar?.elevation = 0F

        var db = Firebase.firestore
        //Picasso.setSingletonInstance(Picasso.Builder(this).build())

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("1059667677594-ucidp245f7pee8vd8t16eq64i337mcgd.apps.googleusercontent.com")
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        account = GoogleSignIn.getLastSignedInAccount(this)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (!prefs.getBoolean(account?.email, false)) {
            // Create UserModel In Firebase For First Time
            db = Firebase.firestore
            val email = account?.email
            if (email != null) {
                db.collection("users").document(email).set(
                    UserModel(
                        email = email,
                        name = account?.displayName.toString(),
                        itemsInCart = listOf("first"))
                ).addOnSuccessListener {
                    Toast.makeText(this, "Data Added", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Data Not Added", Toast.LENGTH_SHORT).show()
                }
            }

            //Store All The Products In Room Database For First Time

            /*val roomDatabase = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                email
            )
                .allowMainThreadQueries()
                .build()

            val entityObject = roomDatabase.daoObject()*/

            /*db.collection("allDryFruitsProducts")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val receivedDataFromFirebase = document.toObject(SingleItemModel::class.java)
                            val dataToStoreInRoomDatabase = receivedDataFromFirebase.itemName?.let {
                                DryFruitItemInRoomDatabase(
                                    itemInTheCart = false,
                                    itemDescription = receivedDataFromFirebase.itemDescription,
                                    itemImageUrl = receivedDataFromFirebase.itemImageUrl,
                                    itemMRP = receivedDataFromFirebase.itemMRP,
                                    itemRetailPrice = receivedDataFromFirebase.itemRetailPrice,
                                    itemName = it
                                )
                            }
                            if (dataToStoreInRoomDatabase != null) {
                                entityObject.addDryFruitsItems(dataToStoreInRoomDatabase)
                            }
                        }
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.exception)
                    }
                }*/

            val editor = prefs.edit()
            editor.putBoolean(account?.email, true)
            editor.apply()
        }

        val navigationView = findViewById<NavigationView>(R.id.drawer_navigation_view)
        val name = navigationView.getHeaderView(0).findViewById<TextView>(R.id.drawer_header_profile_name)
        val emailId = navigationView.getHeaderView(0).findViewById<TextView>(R.id.drawer_header_profile_email_id)
        val profileImage = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.drawer_header_profile_image)


        if (account != null) {
            Picasso.get().load(account!!.photoUrl)
                .into(profileImage)
            emailId.text = account?.email
            name.text = account?.givenName
        }

        //Set MarqueeText

        val marqueeCollectionRef = db.collection("extras")
        val marqueeDocRef = marqueeCollectionRef.document("marqueeText")
        val marqueeTextRef = findViewById<TextView>(R.id.marqueeText)

        marqueeDocRef.addSnapshotListener { value, error ->
            if (value != null) {
                if (value.exists()) {
                    marqueeTextRef.text = value.get("text").toString()
                }
            }
        }
        marqueeTextRef.isSelected = true


        /*val db = Firebase.firestore
        val editText = findViewById<EditText>(R.id.product_name)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val dryFruitItem = SingleItemModel(
                itemName = editText.text.toString(),
                itemDescription = "",
                itemAvailableInStock = true,
                itemImageUrl = "",
                itemMRP = "",
                itemRetailPrice = ""
            )
            db.collection("allDryFruitsProducts").document(editText.text.toString())
                .set(dryFruitItem)
                .addOnSuccessListener {
                    Toast.makeText(this,"Data Added",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Data Cant Be Added",Toast.LENGTH_SHORT).show()
                }
        }*/


        drawerLayout = findViewById(R.id.drawer_layout)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<NavigationView>(R.id.drawer_navigation_view)
            .setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.frontPageFragment,
                R.id.contactUsFragment,
                R.id.aboutUsFragment,
                R.id.bulkOrdersFragment
            ),
            drawerLayout = drawerLayout
        )
        setupActionBarWithNavController(
            navController = navController,
            configuration = appBarConfiguration
        )

        navigationView.menu.findItem(R.id.drawer_log_out).setOnMenuItemClickListener { menuItem:MenuItem? ->
            //write your implementation here
            //to close the navigation drawer
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        Log.d("LogOutInCreate", "onCreate: Clicked")
        mGoogleSignInClient.signOut().addOnCompleteListener {
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
            Toast.makeText(applicationContext, "single item click listener implemented", Toast.LENGTH_SHORT).show()
            finish()
        }
            true
        }

        findViewById<NavigationView>(R.id.drawer_navigation_view).setupWithNavController(
            navController
        )



    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

    override fun onStart() {
        super.onStart()

        if (account == null) {
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        val menuItem = menu.findItem(R.id.cart) as MenuItem
        actionView = menuItem.actionView
        /*val searchView = menu.findItem(R.id.search) as MenuItem
        val searchActionView = searchView.actionView

        val searchEditId = androidx.appcompat.R.id.search_src_text
        val et = searchActionView?.findViewById<EditText>(searchEditId) as EditText

        et.setTextColor(Color.BLACK)
        et.hint = "Search..."
        et.setLinkTextColor(Color.BLACK)
        et.setHintTextColor(Color.BLACK)

        searchActionView.setOnClickListener {
            val intent = Intent(this,SearchActivity::class.java)
            startActivity(intent)
        }*/
        updateCartNumber()

        actionView!!.findViewById<ImageBadgeView>(R.id.cart_menu_icon).setOnClickListener {
            //Toast.makeText(this,"Cart Clicked",Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            bundle.putString("email",account?.email.toString())
            findNavController(R.id.nav_host_fragment).navigate(R.id.cartFragment,bundle)

        }
        //actionView!!.findViewById<ImageBadgeView>(R.id.cart_menu_icon).badgeValue = badgeValue

        /*FrontPageFragment().liveData.observe(this) {
            Log.d("liveDataValue", "onCreateOptionsMenu: $it")

        }*/

        return super.onCreateOptionsMenu(menu)
    }


    private fun updateCartNumber() {
        val email = account?.email
        val db = Firebase.firestore
        val userDocRef = email?.let { db.collection("users").document(it) }
        val cartDocRef = userDocRef?.collection("cart")
        var receivedDataFromFirestore: MutableList<CartModel> = mutableListOf()

        cartDocRef?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }
            receivedDataFromFirestore = mutableListOf<CartModel>()

            for (doc in snapshot!!.documents) {
                val itemName = doc.getString("itemName")
                val itemImageUrl = doc.getString("itemImageUrl")
                val itemPrice = doc.getLong("itemPrice")?.toInt()
                val itemCounter = doc.getLong("itemCounter")?.toInt()
                val cartItem = CartModel(
                    itemImageUrl = itemImageUrl,
                    itemName = itemName,
                    itemPrice = itemPrice,
                    itemCounter = itemCounter
                )
                receivedDataFromFirestore.add(cartItem)
            }
            actionView!!.findViewById<ImageBadgeView>(R.id.cart_menu_icon).badgeValue = receivedDataFromFirestore.size
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.searchFragment)
//                val intent = Intent(this,SearchActivity::class.java)
//                startActivity(intent)
            }
            R.id.cart_menu_icon -> {
                Toast.makeText(this, "Cart Clicked", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }



    override fun addToCartOfFragmentToActivity(data: CartModel?) {

        val email = account?.email
        val db = Firebase.firestore
        val userDocRef = email?.let { db.collection("users").document(it) }
        val cartDocRef = data?.itemName?.let { userDocRef?.collection("cart")?.document(it) }

        cartDocRef?.set(data)

        updateCartNumber()
/*
        Log.d("Email", "onAddToCartClicked: $email")
        if (task == "add") {
            if (email != null) {
                db.collection("users")
                    .document(email)
                    .update("itemsInCart", FieldValue.arrayUnion(itemName))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Item Added In Cart", Toast.LENGTH_SHORT).show()
                        updateCartNumber()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Data Not Added", Toast.LENGTH_SHORT).show()
                    }
            }
        }*/
        /*else {
            if (email != null) {
                db.collection("users")
                    .document(email)
                    .update("itemsInCart", FieldValue.arrayRemove(itemName))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Item Removed From Cart", Toast.LENGTH_SHORT).show()
                        updateCartNumber()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Data Not Added", Toast.LENGTH_SHORT).show()
                    }
            }
        }*/

        //Log.d("received", s!!)
        //badgeValue = s.toInt()
        //Log.d("badgeValue", "addToCartOfFragmentToActivity: $badgeValue")
        //actionView!!.findViewById<ImageBadgeView>(R.id.cart_menu_icon).badgeValue = badgeValue
    }

    override fun deleteFromCartOfFragmentToActivity(itemName: String) {
        val email = account?.email
        val db = Firebase.firestore
        val userDocRef = email?.let { db.collection("users").document(it) }
        val cartDocRef = userDocRef?.collection("cart")?.document(itemName)

        cartDocRef?.delete()


    }

}
