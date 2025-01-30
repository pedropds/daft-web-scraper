# Daft Web Scraper

## Project Overview

The **Daft Web Scraper** is a Scala-based project designed to fetch and filter rental property listings from the [Daft.ie](https://www.daft.ie) website. The application allows users to interactively search, filter, and select properties based on various criteria, such as location, price range, and number of bedrooms. Results are fetched and displayed with options to export property information to a file.

### Key Features

- **Location-based Property Search:** Select one or multiple locations to target specific areas.
- **Price Filtering:** Set minimum and maximum rental price limits.
- **Bedroom Filtering:** Specify the desired range of bedrooms.
- **Pagination:** Navigate through available property listings.
- **Interactive Terminal Interface:** Provides an easy-to-use, interactive terminal session for user selections.
- **Property Export:** Save property listings to a file for further reference.

---

## Project Structure

```
src
├── main
│   └── scala
│       ├── cache
│       │   └── DaftPlaceCache.scala
│       ├── scraper
│       │   ├── DaftPlaceScraper.scala
│       │   └── DaftPropertyScraper.scala
│       ├── service
│       │   ├── BrowserAutomationService.scala
│       │   ├── DaftPlaceService.scala
│       │   ├── DaftPropertyService.scala
│       │   ├── DaftServiceTrait.scala
│       │   └── TerminalService.scala
│       └── util
│           └── EnvLoader.scala
│
├── main.scala
└── test
```

### Key Components

- **DaftPlaceCache.scala:** Handles caching of place data to improve efficiency.
- **DaftPlaceScraper.scala:** Extracts place-related data from Daft.ie.
- **DaftPropertyScraper.scala:** Handles the logic for constructing search URLs, fetching HTML content, and extracting property links.
- **BrowserAutomationService.scala:** Manages browser-based operations if required.
- **DaftPlaceService.scala:** Coordinates the extraction of property places and caching.
- **DaftPropertyService.scala:** Manages property extraction and file exports.
- **TerminalService.scala:** Provides an interactive CLI for filtering and selecting properties.
- **EnvLoader.scala:** Handles environment configurations.
- **Main.scala:** Entry point for the application.

---

## Getting Started

### Prerequisites

- **Scala 2.13 or later**
- **sbt (Scala Build Tool)**
- **Java 11 or later**
- Internet connection

### Installation

1. Clone this repository:
   ```bash
   git clone https://github.com/your-username/daft-web-scraper.git
   cd daft-web-scraper
   ```
2. Run the project using `sbt`:
   ```bash
   sbt run
   ```

---

## Usage

Upon starting the application, the following steps guide the user:

1. **Select Locations:**
    - Search or select from a list of available places.
2. **Set Filters:**
    - Define minimum and maximum price ranges.
    - Specify the range for the number of bedrooms.
3. **Export Property Links:**
    - Save the list of property links to a text file.
4. **Interactive Commands:**
    - Navigate pages and clear filters within the terminal.

---

### Example Session

```
Found 243 places. Starting interactive session...

Available Places (Page 1 of 13):
1. Dublin (County) (1005 rentals)
2. Dublin City (908 rentals)
...
Choose an option: 1
Enter a search term: South Dublin
Available Places (Page 1 of 1):
4. South Dublin City, Dublin (613 rentals)
Choose an option: 4
Filters cleared. Showing all places.
```

---

## Configuration

The following configuration options are available in `DaftPropertyScraper.scala`:

- **Page Size:** Number of listings per page (`PageSize`)
- **Price Range:** Adjustable via `minPrice` and `maxPrice`
- **Bedroom Range:** Configurable via `minBed` and `maxBed`

---

## Saving Properties to a File

To save the fetched property links to a file:

1. The property links are displayed as part of the interactive session.
2. They are stored by calling `DaftPropertyService.savePropertiesToFile()`
   ```bash
   Properties successfully written to properties.txt
   ```

---

## Clearing Terminal

To clear the terminal during the interactive session, the `clearTerminal()` function attempts to issue platform-specific commands (`clear` for Unix-like systems, `cls` for Windows). This feature is included in the `TerminalService.scala`.

---

## Troubleshooting

### Common Issues

#### Empty Property List

Check if the Daft.ie website structure has changed or if network issues are preventing the scraper from accessing content.

#### Pagination Stops Unexpectedly

Ensure that the `from` parameter logic correctly tracks page state.

---

## Future Enhancements

- Enhanced error handling and logging.
- Improved user interface.
- Support for additional search filters.
- Integration with other property websites.

---

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

