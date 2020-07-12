{
  "options": [
  [# th:switch="${quantityType}" ]
  [# th:case="'SCALAR'" ]
    {
        "text": [[ #{home.exact} ]],
        "value": "EXACT"
    }
  [/]
  [# th:case="'RANGE'" ]
    {
        "text": [[ #{home.atLeast} ]],
        "value": "AT_LEAST"
    },
    {
        "text": [[ #{home.atMost} ]],
        "value": "AT_MOST"
    }
  [/]
  [/]
  ]
}