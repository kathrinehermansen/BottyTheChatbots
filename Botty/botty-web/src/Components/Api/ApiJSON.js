import * as React from 'react';

import { JsonView, darkStyles, defaultStyles } from 'react-json-view-lite';
import 'react-json-view-lite/dist/index.css';



const ApiJSON = ({data}) => {
  return (
    <React.Fragment>
      <JsonView data={data} shouldInitiallyExpand={(level) => false} style={defaultStyles} />
    </React.Fragment>
  );
};

export default ApiJSON;