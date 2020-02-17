package in.slanglabs.slang.voicebot;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import java.util.HashSet;
import java.util.Locale;

import in.slanglabs.platform.SlangBuddy;
import in.slanglabs.platform.SlangBuddyOptions;
import in.slanglabs.platform.SlangLocale;
import in.slanglabs.platform.SlangSession;
import in.slanglabs.platform.action.SlangUtteranceAction;

public class SlangInterface {
    private static final String TAG = "SlangInterface";
    private static ActionHandler sActionHandler;

    public interface ActionHandler {
        void onReady();
        void onAction(String spokenText, String englishText);
        void onError(String errorMessage);
    }

    public static void init(
            Application application,
            String buddyId,
            String apiKey,
            Locale defaultLocale,
            ActionHandler actionHandler
    ) {
        sActionHandler = actionHandler;
        try {
            SlangBuddyOptions option = new SlangBuddyOptions.Builder()
                    .setApplication(application)
                    .setBuddyId(buddyId)
                    .setAPIKey(apiKey)
                    .setDefaultLocale(defaultLocale)
                    .setRequestedLocales(new HashSet<Locale>() {
                        {
                            add(SlangLocale.LOCALE_ENGLISH_IN);
                            add(SlangLocale.LOCALE_HINDI_IN);
                        }
                    })
                    .enableEnhancedSpeechRecognition(true)
                    .setEnvironment(SlangBuddy.Environment.STAGING)
                    .setListener(new BuddyListener())
                    .setUtteranceAction(new UtteranceActionHandler())
                    .setAutomaticHelpDisplayThreshold(100)
                    .build();
            SlangBuddy.initialize(option);
        } catch (SlangBuddyOptions.InvalidOptionException e) {
            e.printStackTrace();
            sActionHandler.onError(e.getMessage());
        } catch (SlangBuddy.InsufficientPrivilegeException e) {
            e.printStackTrace();
            sActionHandler.onError(e.getMessage());
        }
    }

    public static void show(Activity activity) {
        SlangBuddy.getBuiltinUI().show(activity);
    }

    private static class BuddyListener implements SlangBuddy.Listener {
        @Override
        public void onInitialized() {
            Log.d(TAG, "onInitialized");
            try {
                SlangBuddy.registerIntentAction("slang_help", null);
                SlangBuddy.getBuiltinUI().disableOnboarding();
                SlangBuddy.getBuiltinUI().disableHelpWindowOnTimeout();
            } catch (SlangBuddy.InvalidIntentException e) {
                e.printStackTrace();
            } catch (SlangBuddy.UninitializedUsageException e) {
                e.printStackTrace();
            }

            sActionHandler.onReady();
        }

        @Override
        public void onInitializationFailed(SlangBuddy.InitializationError e) {
            Log.d(TAG, "onInitializationFailed:" + e.getMessage());
        }

        @Override
        public void onLocaleChanged(Locale newLocale) {
            Log.d(TAG, "onLocaleChanged:" + newLocale.toString());
        }

        @Override
        public void onLocaleChangeFailed(Locale newLocale, SlangBuddy.LocaleChangeError e) {
            Log.d(TAG, "onLocaleChangeFailed:" + newLocale.toString() + ":"  + e.getMessage());
        }
    }

    private static class UtteranceActionHandler implements SlangUtteranceAction {
        @Override
        public void onUtteranceDetected(String userUtterance, SlangSession session) {}

        @Override
        public Status onUtteranceUnresolved(String userUtterance, SlangSession session) {
            sActionHandler.onAction(userUtterance, session.getCurrentUtterance(SlangLocale.LOCALE_ENGLISH_IN));
            return Status.SUCCESS;
        }
    }
}
