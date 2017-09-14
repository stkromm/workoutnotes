package kromm.com.workoutnotes.workout;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.Serializable;

import kromm.com.workoutnotes.R;
import kromm.com.workoutnotes.model.Exercise;
import kromm.com.workoutnotes.model.Workout;

public class WorkoutActivity extends AppCompatActivity implements Serializable
{
    private Exercise selectedExercise;
    private Workout workout;

    private ScreenSlidePagerAdapter adapter;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    private Snackbar snackbar;

    public ViewPager getPager()
    {
        return pager;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        final View layout = findViewById(R.id.root_view);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        workout = new Workout("Workout");
        setTitle(workout.getDisplayName());
        workout.addExercise(new Exercise("Benchpress"));
        workout.addExercise(new Exercise("Pull-Ups"));
        workout.addListener(new Workout.WorkoutChangeListener()
        {
            @Override
            public void onChange(Workout wo, Exercise exercise, boolean removed)
            {
                if (exercise != null && !removed && workout.numberOfExercises() == 1) {
                    adapter.exerciseFragment = new ExercisePageFragment();
                }
                final Exercise exercise1 = exercise;
                if (exercise != null && removed) {
                    snackbar = Snackbar.make(layout, "Removed exercise", Snackbar
                            .LENGTH_INDEFINITE).setAction("Undo", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            workout.addExercise(exercise1);
                        }
                    });
                    snackbar.show();
                } else if (exercise != null && !removed) {
                    if (snackbar != null) {
                        snackbar.dismiss();
                    }
                }
            }
        });
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        // Setup Workout page
        Bundle bundle = new Bundle();
        bundle.putParcelable("Workout", workout);
        adapter.workoutFragment.setArguments(bundle);

        // Setup Exercise page
        bundle = new Bundle();
        bundle.putParcelable("Exercise", workout.getExercise(0));
        adapter.exerciseFragment = new ExercisePageFragment();
        adapter.exerciseFragment.setArguments(bundle);

        pagerAdapter = adapter;
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                if (snackbar != null) {
                    snackbar.dismiss();
                }
                if (position == 0) {
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        pager.setCurrentItem(0);
    }

    public Workout getWorkout()
    {
        return workout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_workout, menu);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        if (pager.getCurrentItem() == 1) {
            selectPreviousExercise();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setSelectedExercise(Exercise selectedExercise)
    {
        if (selectedExercise == null) {
            pager.setCurrentItem(0);
        } else {
            this.selectedExercise = selectedExercise;
            adapter.exerciseFragment.setExercise(selectedExercise);
        }
    }

    public void selectPreviousExercise()
    {
        if (selectedExercise == null) {
            pager.setCurrentItem(0);
            return;
        }
        adapter.workoutFragment.setUpView();
        int index = workout.getIndex(selectedExercise) - 1;
        if (index < 0) {
            pager.setCurrentItem(0);
        } else {
            selectedExercise = workout.getExercise(index);
            adapter.exerciseFragment.setExercise(selectedExercise);
        }
    }

    public void selectNextExercise()
    {
        if (selectedExercise == null) {
            pager.setCurrentItem(0);
            return;
        }
        adapter.workoutFragment.setUpView();
        int index = workout.getIndex(selectedExercise) + 1;
        if (index >= workout.numberOfExercises()) {
            pager.setCurrentItem(0);
        } else {
            selectedExercise = workout.getExercise(index);
            adapter.exerciseFragment.setExercise(selectedExercise);
        }
    }

    public ScreenSlidePagerAdapter getAdapter()
    {
        return adapter;
    }

    public Exercise getSelectedExercise()
    {
        return selectedExercise;
    }

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
    {
        public ScreenSlidePagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        WorkoutPageFragment workoutFragment = new WorkoutPageFragment();
        ExercisePageFragment exerciseFragment = new ExercisePageFragment();

        @Override
        public CharSequence getPageTitle(int position)
        {
            if (position == 0) {
                return "Course";
            } else {
                return "Exercise";
            }
        }

        @Override
        public Fragment getItem(int position)
        {
            if (position == 0) {
                return workoutFragment;
            } else {
                return exerciseFragment;
            }
        }

        @Override
        public int getCount()
        {
            return workout.numberOfExercises() != 0 ? 2 : 1;
        }
    }
}
