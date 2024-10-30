# Currency Exchanger

Currency Exchanger is an Android application designed to convert currencies by fetching the latest exchange rates from [Exchange Rates API](https://exchangeratesapi.io/). The project is developed in Kotlin and utilizes Retrofit for networking.

**`Release APK`**: [app/release/app-release.apk](app\release\app-release.apk)


## Demo
Watch the demo video to see Currency Exchanger in action:
[Video demo](https://drive.google.com/file/d/1pyYFyuD4_9yo34Nv6tWC66HqEj7QE_RZ/view?usp=drive_link)

## Project Structure
The project is organized into several directories for a modular, scalable design.
```graphql

/
│   CurrencyExchangeActivity.kt                # Main activity
│   
├───api
│       CurrencyServiceApi.kt                  # Retrofit API interface for exchange rates
│       
├───dialog
│       ChooseCurrencyListAdapter.kt           # Adapter for displaying currencies in a list dialog
│       ChooseCurrencyListDialog.kt            # Dialog to choose a currency from the list
│
├───fragments
│       CurrencyResultListFragment.kt          # Fragment to display exchange rate results
│
├───helper
│       NetworkHelper.kt                       # Helper class for network-related functions
│       SaveHelper.kt                          # Helper for saving data locally
│       StringHelper.kt                        # String utilities for formatting and processing
│
├───instances
│       RetrofitInstance.kt                    # Singleton instance for initializing Retrofit
│
├───models
│       CurrencyResultList.kt                  # Data class representing a list of currency results
│       ExchangeRatesResponse.kt               # Data class for the API's exchange rate response
│       SymbolsResponse.kt                     # Data class for supported symbols response
│
├───ui
│   └───theme
│           Color.kt                           # Theme colors
│           Theme.kt                           # Main theme settings
│           Type.kt                            # Text styles
│
└───view_models
        CurrencyConverterViewModel.kt          # ViewModel for currency conversion logic
        ExchangeRateResponseViewModel.kt       # ViewModel to manage exchange rate response
        SymbolsViewModel.kt                    # ViewModel to manage symbols and currencies

```

## API Integration
Currency Exchanger uses Retrofit to make network requests to the Exchange Rates API. The API provides the latest exchange rates for supported currency pairs.

## Endpoint
The base URL for the API requests is:

```arduino
https://api.exchangeratesapi.io
```

- ```/latest```
- ```/historical```

API documentation: [https://exchangeratesapi.io/documentation/](https://exchangeratesapi.io/documentation/)

## Getting Started

1. Clone the repository:

```bash
git clone https://github.com/truongpnx/CurrencyExchange.git
```

2. Obtain an API key from exchangeratesapi.io and add it to your Retrofit instance or as a header in the API calls.

Navigate to ```app/apikey.properties``` and set your API key:
```properties
API_KEY=your_api_key
```

3. Run the project in Android Studio to build and deploy the app on an Android device or emulator.

## Usage
 - Select the base currency from the dialog.
 - Select the target currency and input an amount.
 - View the converted exchange rate in real-time.
