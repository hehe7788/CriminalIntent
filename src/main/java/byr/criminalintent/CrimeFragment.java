package byr.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


import byr.criminalintent.javabean.Crime;
import byr.criminalintent.javabean.CrimeLab;
import byr.criminalintent.javabean.Photo;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment.Log";
    public static final String EXTRA_CRIME_ID = "byr.criminalintent.crime_id";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_DATE_TIME = 11;
    private static final String DIALOG_DATE_TIME = "date&time";
    private static final int REQUEST_PHOTO = 12;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private SimpleDateFormat sd;
    private Button mDateTimeButton;

    private UUID mCrimeId;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;


    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(mCrimeId);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment_crime_list for this fragment
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        if (NavUtils.getParentActivityName(getActivity()) != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mDateTimeButton = (Button) v.findViewById(R.id.crime_date_time);

        //"yyyy年MM月dd日 E HH:mm"
        sd = new SimpleDateFormat("yyyy年MM月dd日 E HH:mm", Locale.CHINA);
//        Log.e(TAG, sd.format(mCrime.getDate()));

//        mDateButton.setText(sd.format(mCrime.getDate()));
//        mDateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
//                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
//                dialog.show(fm, DIALOG_DATE);
//            }
//        });

        mDateTimeButton.setText(sd.format(mCrime.getDate()));
        mDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ChoosePickerFragment dialog = ChoosePickerFragment.newInstance(mCrime.getDate());
                //设置本fragment为要启动的ChoosePickerFragment的目标fragment，requestCode为REQUEST_DATE_TIME
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE_TIME);
                dialog.show(fm, DIALOG_DATE_TIME);
            }
        });
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });
        //查询是否有相机
        PackageManager pm = getActivity().getPackageManager();
        boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD ||
                android.hardware.Camera.getNumberOfCameras() > 0;
        if (!hasCamera) {
            mPhotoButton.setEnabled(false);
        }

        mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_list_item_context, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity())!=null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.menu_item_delete:
                Log.e(TAG, "TODO delete");
                //TODO startActivity
                Intent i = new Intent(getActivity(), CrimeListActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("delete.id", mCrimeId);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult", "request " + requestCode + "result " + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            Log.e(TAG, date.toString());
            Log.e(TAG, mCrime.getDate().toString());
            mDateButton.setText(sd.format(mCrime.getDate()));
        }
        //收到了ChoosePickerFragment返回的消息
        if (requestCode == REQUEST_DATE_TIME) {
            Date date = (Date) data.getSerializableExtra(ChoosePickerFragment.EXTRA_DATE_TIME);
            mCrime.setDate(date);
            mDateTimeButton.setText(sd.format(mCrime.getDate()));
        }
        if (requestCode == REQUEST_PHOTO) {
            String fileName = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (fileName != null) {
                Photo photo = new Photo(fileName);
                mCrime.setPhoto(photo);
                Log.e(TAG, "Crime has a photo " + mCrime.getPhoto().getFileName());
            }
        }
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to {@link Activity#onPause() Activity.onPause} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onPause() {
        super.onPause();
        //需要在onPause这里保存！
        CrimeLab.get(getActivity()).saveCrimes();
        Log.e(TAG, "onPause save");
    }
}
